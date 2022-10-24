import org.openrndr.application
import org.openrndr.color.ColorRGBa
import util.RealPx
import util.ScalarField
import kotlin.math.sin
import kotlin.random.Random

fun main() = application {
    configure {
        width = 600
        height = 600
    }
    val seed = 6
    program {
        val rpx = RealPx(this, 0.0, 0.0, 30.0)

        val sf = ScalarField { v ->
            sin(v.x) + sin(v.y)
        }
        val vf = sf.gradient

        val points = makeGrid(50, 50).flatten()

        fun colorMapper(abslim: Double, positiveColor: ColorRGBa, negativeColor: ColorRGBa) = { t: Double ->
            if (t > 0) positiveColor.shade(t / abslim) else negativeColor.shade(-t / abslim)
        }

        extend {
//            drawer.clear(ColorRGBa.WHITE)
            val outlineColorMapper = colorMapper(2.0, ColorRGBa.RED, ColorRGBa.BLUE)

            drawer.strokeWeight = 2.0
            for (point in points) {
                rpx.withReal(point) { v ->
                    drawer.stroke = outlineColorMapper(sf(v))
                    val div = vf.divergence(v)
                    drawer.fill = colorMapper(1.0, ColorRGBa.YELLOW, ColorRGBa.GREEN)(div)
                    drawer.circle(point, 5.0)
                }
            }
        }
    }
}