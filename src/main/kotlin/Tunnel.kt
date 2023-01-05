import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.extras.easing.easeCubicIn
import org.openrndr.extras.easing.easeQuadIn

fun main() = application {
    configure {
//        fullscreen = Fullscreen.SET_DISPLAY_MODE
        width = 1920
        height = 1080
    }

    program {
        class TunnelSquare(val creationTime: Double) {
            val initSize: Double = height.d
            val d by { (seconds - creationTime + 1.0) * 0.2 }
            val angle by { d * 30.0 + d * d * 7.0 + seconds * 10.0 }
            val size by { initSize * easeCubicIn(1.0 / (d * 1.0)) }
            val strokeW by { size / 200.0 }
            val strokeColor: ColorRGBa = ColorHSVa(creationTime * 50, 1.0, 1.0).toRGBa()
            fun draw(drawer: Drawer) = drawer.isolated {
                fill = null
                stroke = strokeColor // set to white to mess with brain
                strokeWeight = strokeW
                rotate(angle)
                translate(-size / 2, -size / 2)
                rectangle(0.0, 0.0, size, size)
            }
        }

        val addTimes = generateSequence(0.0) { it + 0.4 }.iterator()
        val squares = mutableListOf<TunnelSquare>()

        var nextAddTime = addTimes.next()

        val composite = compose {
            draw {
                squares.removeAll { it.strokeW < 0.01 }
                if (seconds > nextAddTime) {
                    squares.add(TunnelSquare(nextAddTime))
                    nextAddTime = addTimes.next()
                }
                drawer.translate(drawer.bounds.center)
                for (s in squares) s.draw(drawer)
            }
            // too demanding for this pc
//            post(GaussianBloom()) {
//                sigma = 2.0
//                passes = 1
//                noiseGain = 0.1
//                window = 3
//            }
        }

        extend {
            composite.draw(drawer)
        }
    }
}