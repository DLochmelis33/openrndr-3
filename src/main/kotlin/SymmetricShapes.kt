import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import kotlin.math.*

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    program {
        val r = Random
        r.seed = kotlin.random.Random.nextLong().toString()

        val symOrder = 8
        val baseHue = r.double0(360.0)
        val shapes = List(2000 / symOrder) {
            object {
                val center = run {
                    val dim = max(width, height).d
                    r.vector2(10.0, dim / 2)
                }
                val radius = r.double(15.0, 50.0) * log2(center.length) / 6.0
                val color = ColorHSVa(baseHue + r.double0(10.0), r.double(0.5), r.double(0.5))
                val offsetCoef = r.double(2.0, 5.0)
                val colorOffsetCoef = r.double()
            }
        }.filterNot { it.center.length < it.radius }
        extend {
            drawer.isolated {
                translate(bounds.center)
                circles {
                    strokeWeight = 0.3
                    shapes.forEach {
                        val color = ColorHSVa(
                            it.color.h,
                            (it.color.s + 0.2 * sin(seconds * it.colorOffsetCoef)).coerceIn(0.0, 1.0),
                            (it.color.v + 0.2 * (cos(seconds * sqrt(2.0) * it.colorOffsetCoef))).coerceIn(0.0, 1.0)
                        )
                        fill = color.toRGBa()
                        for (angle in List(symOrder) { 360.0 / symOrder * it }) {
                            val newCenter = it.center.rotate(angle + seconds * it.offsetCoef / it.radius * 100.0)
                            circle(newCenter, it.radius)
                        }
                    }
                }
            }
        }
    }
}
