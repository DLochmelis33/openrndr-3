import org.openrndr.MouseButton
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.*

const val rows = 7
const val cols = 7

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
        var field: Array<Array<Rotation>> = Array(rows) { Array(cols) { Rotation.UL } }

        fun Drawer.drawField() {
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val at = Vector2(j * size, i * size)
                    quarterCircle(at, size, field[i][j])
                }
            }
            fill = ColorRGBa.PINK
            for (i in 0..rows) {
                for (j in 0..cols) {
                    circle(j * size, i * size, 5.0)
                }
            }
        }

        // (i, j) == DR corner of 2x2 square
        fun quadRotate(i: Int, j: Int, clockwise: Boolean) {
            if (i !in 1 until rows || j !in 1 until cols) return
            val froms = listOf(Pair(i - 1, j - 1), Pair(i - 1, j), Pair(i, j), Pair(i, j - 1))
            val tos = froms.rotate(if (clockwise) 1 else -1)

            val tmp = field.deepCopy()
            for (t in 0..3) {
                tmp[tos[t]] = field[froms[t]].rotated(clockwise)
            }
            field = tmp
        }

        mouse.buttonDown.listen {
            val cells = (1 until rows).toList() * (1 until cols).toList()
            val (i, j) = cells.minByOrNull { (i, j) ->
                it.position.distanceTo(
                    Vector2(
                        j * size,
                        i * size
                    )
                )
            }!! // TODO x2
            if (it.position.distanceTo(Vector2(j * size, i * size)) > size / 2.1) return@listen
//            val i = (it.position.y * cols / height).toInt()
//            val j = (it.position.x * rows / width).toInt()
            val clockwise = it.button == MouseButton.LEFT
//            field[i][j] = field[i][j].rotated(clockwise)
            quadRotate(i, j, clockwise)
        }
        extend {
            drawer.run {
                drawField()
            }
        }
    }
}

