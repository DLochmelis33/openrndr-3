import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extras.color.presets.NAVY
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    program {
        Random.randomizeSeed()

        val color1 = ColorRGBa.PINK
        val color2 = ColorRGBa.NAVY
        fun colorAt(v: Vector2): ColorRGBa {
            val t = v.y / height
//            val a = 1.0 / (exp(-2.0 * (t - 0.5)) + 1)
            val a = Random.gaussian(t, 0.1).coerceIn(0.0, 1.0)
            return color1.mix(color2, a)
        }

        val circles = mutableListOf(
            Circle(width * 0.5, height * 0.5, 100.0) to colorAt(drawer.bounds.center)
        )
        val bounds = drawer.bounds
        GlobalScope.launch {
            while (true) {
                val p = Random.point(bounds)
                Random.isolated {
                    val (c, _) = circles.minByOrNull { (c, _) ->
                        c.center.distanceTo(p) - c.radius * gaussian(1.0, 0.15)
                    }!!
                    val r = c.radius * gaussian(1.0, 0.15)
                    val d = c.center.distanceTo(p) - r
                    if (d > 3.0) {
                        circles.add(Circle(p, d.coerceAtMost(200.0)) to colorAt(p))
                    }
                }
            }
        }

        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                stroke = null
                circles {
                    for ((circle, color) in circles.toList()) {
                        fill = color
                        circle(circle)
                    }
                }
            }
        }
    }
}