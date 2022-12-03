import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.math.Spherical
import org.openrndr.math.Vector2
import org.openrndr.shape.contour

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    program {

        class Leaf(val sphereRadius: Double, val basisUnit: Double) {
            var isDone = false
                private set

            fun draw(drawer: Drawer, seconds: Double) {
                if (isDone) return
                val theta = -90 + seconds * 20.0
                if (theta > 90.0) {
                    isDone = true
                    return
                }
                val phi = seconds * 5.0 + 70.0

                val stem = Spherical(theta, phi, sphereRadius).cartesian
                val a = Spherical(theta + 90.0, 90.0, basisUnit).cartesian
                val b = Spherical(theta, phi + 90, basisUnit).cartesian

                val squareCorners = listOf(
                    a + b,
                    a - b,
                    -a - b,
                    -a + b
                ).map { it.xy }

                val projSquare = contour {
                    moveTo(squareCorners[0])
                    for (i in 1..3) lineTo(squareCorners[i])
                    close()
                }

                drawer.isolated {
                    fill = ColorRGBa.GREEN
                    stroke = ColorRGBa.WHITE
                    translate(stem.xy)
                    contour(projSquare)
                }
            }
        }

        val leaf = Leaf(200.0, 50.0)

        val composite = compose {
            draw {
                drawer.translate(drawer.bounds.center)
                leaf.draw(drawer, seconds)
            }
            post(GaussianBloom()) {
                sigma = 2.0
                passes = 2
                noiseGain = 0.1
                gain = 2.0
                window = 10
            }
        }

        extend {
            composite.draw(drawer)
        }
    }
}