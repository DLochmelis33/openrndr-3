import org.openrndr.KEY_BACKSPACE
import org.openrndr.KEY_DELETE
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.math.Vector2
import kotlin.math.max
import kotlin.random.Random

const val randomSeed: Int = 5

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    val random = Random(randomSeed)

    var useBloom = false
    var doClear = false

    val circles = mutableListOf<Vector2>()
    var curSize = 10.0

    program {
        keyboard.keyDown.listen {
            when (it.key) {
                KEY_SPACEBAR -> useBloom = !useBloom
                67 -> doClear = true
                else -> println(it.key)
            }
        }
        mouse.dragged.listen {
            circles.add(it.position)
        }
        mouse.scrolled.listen {
            curSize += it.rotation.y
            curSize = max(1.0, curSize)
        }

        val bloom = GaussianBloom().apply {
            gain = 2.0
            shape = 0.0 // size but works at 0.0
            sigma = 5.0 // also size?
            window = 50
            noiseGain = 0.0
        }

        val rt1 = renderTarget(width, height) { colorBuffer() }
        val rt2 = renderTarget(width, height) {
            colorBuffer()
        }

        extend {
            drawer.isolatedWithTarget(rt1) {
                drawer.fill = ColorRGBa.WHITE
                drawer.stroke = null

                drawer.circles(circles, curSize)

                if (doClear) {
                    doClear = false
                    circles.clear()
                    drawer.clear(ColorRGBa.BLACK)
                }

            }

            if (useBloom) {
                bloom.apply(rt1.colorBuffer(0), rt2.colorBuffer(0))
                drawer.image(rt2.colorBuffer(0))
            } else {
                drawer.image(rt1.colorBuffer(0))
            }
            drawer.circle(mouse.position, curSize)

            rt1.colorBuffer(0).fill(ColorRGBa.BLACK)
            rt2.colorBuffer(0).fill(ColorRGBa.BLACK)
        }
    }
}
