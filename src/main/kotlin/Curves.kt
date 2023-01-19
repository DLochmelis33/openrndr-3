import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour
import kotlin.random.Random

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    val random = Random
    program {
        fun gen(): ShapeContour {
            val controlPoints = List(6) { random.point(drawer.bounds) }
            return contour {
                moveTo((controlPoints.first() + controlPoints.last()) / 2.0)
                controlPoints.zipNextCyclic().forEach { (p1, p2) ->
                    curveTo(p1, (p1 + p2) / 2.0)
                }
                close()
            }
        }
        var curve = gen()
        keyboard.keyDown.listen {
            if(it.key == KEY_SPACEBAR) curve = gen()
        }
        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                strokeWeight = 10.0
                stroke = ColorRGBa.RED
                contour(curve)
            }
        }
    }
}
