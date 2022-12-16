import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import util.multisampling
import util.videoExtend
import kotlin.math.PI
import kotlin.math.sin

private data class Params(val side: Double, val angle: Double, val colorRGBa: ColorRGBa)

fun main() = application {
    configure {
        width = 1920
        height = 1080
        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    program {
        val size = 180.0
        val xs = generateSequence(-size) { it + size }.takeWhile { it <= width + size }.toList()
        val ys = generateSequence(-size) { it + size }.takeWhile { it <= height + size }.toList()
        val centersGrouped = xs.withIndex().flatMap { (ix, x) ->
            ys.withIndex().map { (iy, y) ->
                Pair((ix + iy) % 2, Vector2(x, y))
            }
        }.groupBy({ it.first }, { it.second })

        val color1 = ColorRGBa.PINK
        val color2 = ColorRGBa.PINK.mix(ColorRGBa.YELLOW, 0.3)

//        videoExtend { _, seconds ->
        extend {
            val t = (sin(seconds * 0.8) + 1) / 2 // in [0, 1]
//            if(seconds * 0.8 >= 2 * PI) return@videoExtend false

            drawer.multisampling(8) {
                clear(ColorRGBa.WHITE.shade(0.1))

                for ((group, centers) in centersGrouped) {
                    val (side, angle, color) = when (group) {
                        1 -> Params(size * (0.7 + t * 0.8), 45.0 + t * 135.0, color1.mix(color2, t))
                        0 -> Params(size * (0.9 + (1 - t) * 0.4), (1 - t * 2) * 90.0, color1.mix(color2, 1 - t))
                        else -> error("unreachable")
                    }

                    fill = color
                    stroke = ColorRGBa.WHITE
                    strokeWeight = 3.0

                    for (center in centers) {
                        isolated {
                            translate(center)
                            rotate(angle)
                            translate(-side / 2, -side / 2)
                            rectangle(Vector2.ZERO, side, side)
                        }
                    }
                }
            }
//            return@videoExtend true
        }
    }
}