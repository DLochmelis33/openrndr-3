import org.openrndr.Fullscreen
import org.openrndr.KEY_ESCAPE
import org.openrndr.application
import org.openrndr.math.Vector2
import util.RealPx
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess

private const val rows = 720 / 4
private const val cols = 1080 / 4
private const val speed = 0.01

private val seed: Long = 5
private fun u(x: Double, y: Double) = randomScope(seed) { nrng * sin(nrng * x + nrng * y) }
private fun v(x: Double, y: Double) = randomScope(seed + 1) { nrng * cos(nrng * x + nrng * y) }

private fun vf(p: Vector2): Vector2 {
    val (x, y) = p
    return Vector2(u(x, y), v(x, y))
}

fun main() = application {
    configure {
        width = 1920
        height = 1080
        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }

    program {
        val rp = RealPx(this, 0.0, 0.0, 100.0)
        keyboard.keyDown.listen { if (it.key == KEY_ESCAPE) exitProcess(0) }

        var pts = makeGrid(width * 3, height * 3, rows, cols).flatten().map { it - Vector2(width.d, height.d) }

        extend {
            pts = pts.map {
                rp.realScope(it) {
                    t + vf(t) * speed
                }
            }
            drawer.stroke = null
            drawer.circles(pts, 3.0)
        }
    }
}