import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.renderTarget
import org.openrndr.extra.noise.perlin
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.easing.easeCubicInOut
import org.openrndr.math.Vector2
import util.RealPx
import util.SF
import kotlin.math.*
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val seed = Random.nextInt()
        val sf: SF = { v ->
            val n = 95
            List(n) { simplex(seed + it, v).normalized() }.minOf { it } * sqrt(n.d) * 0.8
        }
        val rpx = RealPx(width, height, 0.0, 0.0, 100.0)
        val rt = renderTarget(width, height) { colorBuffer() }
        val cb = rt.colorBuffer(0)
        cb.shadowContext { img ->
            for (x in 0 until width) {
                for (y in 0 until height) {
                    rpx.withReal(Vector2(x.d, y.d)) { v ->
                        img[x, y] = ColorRGBa.BLUE.shade(easeCubicInOut(sf(v).coerceIn(0.0, 1.0)))
                    }
                }
            }
        }
        extend {
            drawer.image(cb)
        }
    }
}
