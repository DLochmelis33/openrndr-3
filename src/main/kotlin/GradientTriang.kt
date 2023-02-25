import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.shadestyles.linearGradient
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.extras.color.presets.DARK_VIOLET
import org.openrndr.extras.color.presets.LIGHT_BLUE
import org.openrndr.math.Vector2

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    val r = org.openrndr.extra.noise.Random
    program {
        val points = List(25) { r.point(drawer.bounds) } + listOf(
            0 to 0,
            width to 0,
            0 to height,
            width to height
        ).map { (x, y) -> Vector2(x.d, y.d) }
        val triangles = Delaunay.from(points).triangles()
        val gradRotCoef = triangles.map { r.double(-1.0, 1.0) }
        extend {
            drawer.isolated {
                strokeWeight = 0.01
                for ((t, grc) in triangles zip gradRotCoef) {
                    shadeStyle = linearGradient(
                        ColorRGBa.LIGHT_BLUE,
                        ColorRGBa.DARK_VIOLET,
                        rotation = seconds * 120.0 * grc
                    )
                    shape(t.shape)
                }
            }
        }
    }
}