import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1920
        height = 1080
        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    val r = Random(System.currentTimeMillis())

    program {

        data class Thing(val d: Double, val ang: Double, val rot: Double, val color: ColorRGBa)

        val count = 50
        val rotLim = 10.0
        val absVar = 50.0
        val relVar = 0.1

        val things = List(count) {
            Thing(
                run {
                    val target = width / 2.0 / count * it
                    r.nextDouble(target * (1.0 - relVar) - absVar, target * (1.0 + relVar) + absVar)
                },
                r.nextDouble(0.0, 360.0),
                r.nextDouble(-rotLim, rotLim),
                r.nextRGB()
            )
        }

        val rt = renderTarget(width, height, multisample = BufferMultisample.SampleCount(8)) {
            colorBuffer()
            depthBuffer()
        }
        extend {
            drawer.isolatedWithTarget(rt) {

                drawer.translate(width / 2.0, height / 2.0)
                drawer.strokeWeight = 0.2
                for ((d, ang, rot, color) in things) {
                    drawer.isolated {
                        rotate(ang + rot * seconds)
                        translate(0.0, d)
                        fill = color
                        rectangle(-width.d, 0.0, width * 2.0, height * 2.0)
                    }
                }

            }
            val resolved = colorBuffer(width, height)
            rt.colorBuffer(0).copyTo(resolved)
            drawer.image(resolved)
        }
    }
}