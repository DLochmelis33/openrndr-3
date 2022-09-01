import org.openrndr.MouseButton
import org.openrndr.application
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.*

const val rows = 10
const val cols = 10

enum class Rotation(val angle: Double) {
    UL(0.0), UR(90.0), DR(180.0), DL(270.0);

    fun rotated(clockwise: Boolean): Rotation {
        val values = values()
        val i = values.indexOf(this)
        return values(i + if (clockwise) 1 else -1)
    }
}

fun Drawer.quarterCircle(at: Vector2, size: Double, rotation: Rotation) {
    isolated {
        val quarterShapes = compound {
            difference {
                shape(Circle(at.x + size, at.y + size, size).shape)
                union {
                    shape(Rectangle(at.x + size, at.y, size, size * 2).shape)
                    shape(Rectangle(at.x, at.y + size, size * 2, size).shape)
                }
            }
        }
        val shapeCenter = Vector2(at.x + size / 2.0, at.y + size / 2.0)

        translate(shapeCenter)
        rotate(rotation.angle)
        translate(-shapeCenter)

        shapes(quarterShapes)
    }
}

fun main() = application {
    configure {
        width = 800
        height = 800
    }
    program {
        val size = width.d / cols // TODO
        val field: List<MutableList<Rotation>> = List(rows) { MutableList(cols) { Rotation.UL } }
        fun Drawer.drawField() {
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val at = Vector2(j * size, i * size)
                    quarterCircle(at, size, field[i][j])
                }
            }
        }
        mouse.buttonDown.listen {
            val i = it.position.y.toInt() * cols / height
            val j = it.position.x.toInt() * rows / width
            field[i][j] = field[i][j].rotated(it.button == MouseButton.LEFT)
        }
        extend {
            drawer.run {
                drawField()
//                for (i in 100..400 step 100) {
//                    quarterCircle(
//                        Vector2(i.d, 400.0),
//                        50.0,
//                        run {
//                            var r = Rotation.UL
//                            repeat((i - 1) / 100) { r = r.rotated(true) }
//                            r
//                        })
//                }
            }
        }
    }
}

