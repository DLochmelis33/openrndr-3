import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import util.ScalarField
import util.VectorField
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    oliveProgram {
        val seed = Random.int()
        extend {
            val vf = VectorField {
                val x = 2.0 * it.x / width
                val y = 2.0 * it.y / height
                val speed = 0.05
                Vector2(simplex(seed, x, y, seconds * speed), simplex(seed + 1, x, y, seconds * speed))
            }.zeroDivergent

            val hehe = ScalarField { v ->
                val n = 4 * 8
                val radius = 5.0
                val dTheta = 2 * PI / n
                var integral = 0.0
                for (i in 0 until n) {
                    val theta = i * dTheta
                    val point = v + Vector2(cos(theta), sin(theta)) * radius
                    val vec = Vector2(-sin(theta), cos(theta))
                    integral += vf(point).normalized.dot(vec)
                }
                integral/n
            }

            val points = drawer.bounds.grid(width * 0.02, height * 0.02).flatMap { it.map { it.center } }
            val shifted = points.map { it + vf(it) * 25.0 }
            val segments = (points zip shifted).map { (a, b) -> LineSegment(a, b) }
            val curls = points.map { hehe(it) * 100.3 }

            drawer.isolated {
                stroke = ColorRGBa.PINK
                lineSegments(segments)

                stroke = null
                fill = ColorRGBa.WHITE.opacify(0.3)
                circles(points, curls)
            }
        }
    }
}