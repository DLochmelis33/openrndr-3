import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.shapes.grid
import org.openrndr.extras.color.presets.PURPLE
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import util.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private const val rows = 720 / 15
private const val cols = 1080 / 15
private const val speed = 2.0
private const val scale = 0.002

private val seed = Random.nextInt()
//private fun u(x: Double, y: Double) = simplex(seed, x, y)
//private fun v(x: Double, y: Double) = simplex(seed + 5, x, y)

private val u: SF = { simplex(seed, it) }
private val v: SF = { simplex(seed + 1, it) }

private val vf: VF = { (x, y) ->
//    Vector2(
//        u(x, y) - v(y, x),
//        v(x, y) - u(y, x)
//    )
    Vector2(simplex(seed + 1, y), simplex(seed + 2, x))
}

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    program {
        val pts = Rectangle(-width.d, -height.d, width * 3.0, height * 3.0)
            .grid(cols * 3, rows * 3).flatten().map { it.center }.toMutableList()
        val ptsInit = pts.toList()

        GlobalScope.launch {
            while (true) {
                pts.replaceAll {
                    it + vf(it * scale) * speed
                }
                delay(1.milliseconds)
            }
        }

        extend {
            drawer.circles {
                fill = null
                strokeWeight = 0.1
                for (point in ptsInit) {
                    val d = vf.div(point * scale)
                    stroke = if (d > 0) ColorRGBa.YELLOW.shade(d) else ColorRGBa.PURPLE.shade(-d)
                    circle(point, 1.0)
                }
            }
            drawer.isolated {
                stroke = ColorRGBa.WHITE
                strokeWeight = 2.0
                points(pts)
            }
        }
    }
}
