import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.color.presets.LIME_GREEN
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import kotlin.random.Random

data class ParaPoint(
    val point: Vector2,
    val depth: Double,
    val createdTime: Double,
)

fun main() = application {
    configure {
        width = 1920
        height = 1080
        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    val random = Random
    val seed = random.nextInt()
    program {
        val speed = 5000.0
        val size = 100.0
        val minDepth = 1.5
        val maxDepth = 20.0
        val pointCount = 3000
        val changeSpeed = 0.07

        val moveVec: Vector2 by {
            val x = simplex(seed, 0.0, seconds * changeSpeed)
            val y = simplex(seed, 100.0, seconds * changeSpeed)
            Vector2(x, y).normalized
//            Polar(seconds * 2.0).cartesian
        }

        val genCircle = Circle(drawer.bounds.center, width.d + height.d)
        fun genNewPoint(): ParaPoint {
            val t = Polar.fromVector(-moveVec).theta
            val point = genCircle.center + Polar(t + random.nextDouble(-80.0, 80.0), genCircle.radius).cartesian
            return ParaPoint(point, random.nextDouble(minDepth, maxDepth), seconds)
        }

        fun ParaPoint.calcShift() = moveVec * ((seconds - createdTime) * speed / depth)
        var ps = List(pointCount) {
            ParaPoint(random.point(genCircle), random.nextDouble(minDepth, maxDepth), seconds)
        }.sortedByDescending { it.depth }

        val composite = compose {
            draw {
                drawer.isolated {
                    circles {
                        ps = ps.map { if (genCircle.contains(it.point + it.calcShift())) it else genNewPoint() }
                            .sortedByDescending { it.depth }
                        for (p in ps) {
                            fill = ColorRGBa.WHITE.shade(2.0 / p.depth)
                            stroke = ColorRGBa.RED
                            strokeWeight = 5.0
                            circle(p.point + p.calcShift(), size / p.depth)
                        }
                    }
                }
            }
            post(GaussianBloom()) {
                sigma = 2.0
                passes = 2
                noiseGain = 0.1
                gain = 2.0
                window = 3
            }
        }
        extend {
            composite.draw(drawer)
        }
    }
}