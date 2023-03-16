import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extras.color.presets.BLUE_VIOLET
import org.openrndr.extras.color.presets.LIME_GREEN
import org.openrndr.shape.Rectangle

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val rw = 50
        val stripes = List(height) { Rectangle(0.0, it.d, width.d, it.d) }
        val colors = List(stripes.size) { i ->
            val t = (i.d + Random.int(-rw, rw)).coerceIn(0.0, height.d) / (height.d - 1.0)
            ColorRGBa.LIME_GREEN.mix(ColorRGBa.BLUE_VIOLET, t)
        }
        extend {
            drawer.isolated {
                stroke = null
                this.rectangles {
                    for ((r, c) in stripes zip colors) {
                        fill = c
                        rectangle(r)
                    }
                }
            }
        }
    }
}