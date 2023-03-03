import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.shapes.hobbyCurve
import org.openrndr.shape.Circle
import kotlin.math.sin

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val c1 = Circle(width * 0.2, height * 0.7, 30.0)
        val c2 = Circle(width * 0.5, height * 0.2, 50.0)
        val c3 = Circle(width * 0.8, height * 0.8, 50.0)

        val repeatCount = 1000
        val curves = zip(
            c1.shape.randomPoints(repeatCount),
            c2.shape.randomPoints(repeatCount),
            c3.shape.randomPoints(repeatCount)
        ).map { hobbyCurve(it) }

        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                strokeWeight = 1.0
                stroke = ColorRGBa(0.0, 0.0, 1.0, 0.01)
                fill = null
                contours(curves.map { it.sub((sin(seconds*1.2) + 1.0) / 2.0, (sin(seconds + 1.0) + 1.0) / 2.0) })
            }
        }
    }
}