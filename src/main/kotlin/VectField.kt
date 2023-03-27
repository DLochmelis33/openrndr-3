import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.shapes.grid
import org.openrndr.extras.color.presets.PURPLE
import org.openrndr.math.Vector2
import util.VF
import util.div
import kotlin.random.Random

private const val rows = 720 / 5
private const val cols = 1080 / 5
private const val speed = 2.0
private const val scale = 0.002

private val seed = Random.nextInt()
private fun u(x: Double, y: Double) = simplex(seed, x, y)
private fun v(x: Double, y: Double) = simplex(seed + 1, x, y)

private val vf: VF =  { (x, y) -> Vector2(u(x, y) - v(y, x), v(x, y) - u(y, x)) }

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    program {
        var pts = drawer.bounds.grid(cols, rows).flatten().map { it.center }

        extend {
            pts = pts.map {
                it + vf(it * scale) * speed
            }
            drawer.circles {
                fill = null
                strokeWeight = 0.4
                for (point in pts) {
                    val d = vf.div(point * scale)
                    stroke = if (d > 0) ColorRGBa.YELLOW.shade(d) else ColorRGBa.PURPLE.shade(-d)
                    circle(point, 4.0)
                }
            }
            drawer.isolated {
                stroke = ColorRGBa.WHITE
                strokeWeight = 1.0
                points(pts)
            }
        }
    }
}
