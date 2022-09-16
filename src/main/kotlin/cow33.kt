import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.extras.color.presets.*
import org.openrndr.shape.Circle
import kotlin.math.sin

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    program {
        var r = 0.0
        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.translate(width / 2.0, height / 2.0)
            drawer.rotate(20.0 + r)
            r += 0.1
            drawer.fill = ColorRGBa.GREY// (255.0 / 255.0, 5.0 / 255.0, 164.0/255.0)
            drawer.stroke = ColorRGBa.DARK_GREY
            drawer.strokeWeight = 10.0
            repeat(10) { t ->
                drawer.circle(0.0, 150.0, 20.0)
                drawer.rotate(36.0)

                drawer.lineSegment(0.0, 170.0, 0.0, 800.0)
            }

            drawer.circle(0.0, 0.0, 130.0)
            val c = Circle(0.0, 95.0, 70.0).contour.sub(0.0, 0.5)
            drawer.contour(c)
            drawer.circle(-30.0, -20.0, 10.0)
            drawer.circle(30.0, -20.0, 10.0)
        }
    }
}