import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import util.SF
import util.VF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val VF.customOp: VF
    get() = { p ->
        val curl = Vector2(
            this(Vector2(p.x + 1e-6, p.y)).y - this(Vector2(p.x - 1e-6, p.y)).y,
            this(Vector2(p.x, p.y + 1e-6)).x - this(Vector2(p.x, p.y - 1e-6)).x
        ) / 2e-6
        Vector2(-curl.y, curl.x)
    }

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    oliveProgram {
        val seed = Random.int()
        extend {
            val vf: VF = { v: Vector2 ->
                val x = 2.0 * v.x / width
                val y = 2.0 * v.y / height
                val speed = 0.05
                Vector2(simplex(seed, x, y, seconds * speed), simplex(seed + 1, x, y, seconds * speed))
            }.customOp

            val hehe: SF = { v ->
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
                integral / n
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