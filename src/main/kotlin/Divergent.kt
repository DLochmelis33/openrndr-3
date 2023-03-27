import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.noise.simplex2D
import org.openrndr.extra.noise.simplex3D
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import util.RealPx
import util.VF
import util.div
import util.grad
import kotlin.math.*
import kotlin.random.Random

private const val seed = 5
private val r = Random(seed)

fun main() = application {
    configure {
        width = 600
        height = 600
    }
    program {
        val rpx = RealPx(this, 0.0, 0.0, 100.0)

        val (a0, a1, a2, a3) = List(4) { r.nextDouble(-1.0, 1.0) }
        // supposed to be harmonic
        val realF = { x: Double, y: Double -> a3 * x * x * x - a3 * x * y * y + a2 * x * x - a2 * y * y + a1 * x + a0 }
        val imagF = { x: Double, y: Double -> a3 * x * x * y - a3 * y * y * y + a2 * x * y + a1 * y }

        val vf: VF = { v: Vector2 -> simplex(seed, v) }.grad.div.grad

        val points = makeGrid(30, 30).flatten()
        val ends = points.map { rpx.realScope(it) { t + vf(t) * 0.1 } }
//            val segments = (points zip ends).map { (s, e) -> Segment(s, e) }
        extend {

            for ((point, end) in points zip ends) {
                drawer.stroke = ColorRGBa.WHITE
                drawer.lineSegment(point, end)
                drawer.fill = ColorRGBa.PINK.shade(min(vf.div(rpx.fromPx(point)), 1.0) )
                drawer.stroke = ColorRGBa.PINK
                drawer.circle(point, 3.0)
            }
        }

//        extend {
////            drawer.clear(ColorRGBa.WHITE)
//        fun colorMapper(abslim: Double, positiveColor: ColorRGBa, negativeColor: ColorRGBa) = { t: Double ->
//            if (t > 0) positiveColor.shade(t / abslim) else negativeColor.shade(-t / abslim)
//        }
//            val outlineColorMapper = colorMapper(2.0, ColorRGBa.RED, ColorRGBa.BLUE)
//
//            drawer.strokeWeight = 2.0
//            for (point in points) {
//                rpx.withReal(point) { v ->
//                    drawer.stroke = outlineColorMapper(sf(v))
//                    drawer.fill = colorMapper(1.0, ColorRGBa.YELLOW, ColorRGBa.GREEN)(vf.curl2d(v))
//                    drawer.circle(point, 5.0)
//                }
//            }
//        }
    }
}