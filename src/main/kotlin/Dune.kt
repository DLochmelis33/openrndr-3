import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.colorBuffer
import org.openrndr.extra.noise.perlin
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.color.presets.ORANGE
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.shape.Rectangle
import util.RealPx
import util.toScalarField
import util.toVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    val seed = Random.nextInt()
    fun unitNoise(v: Vector2, sd: Int = seed) = (simplex(sd, v) + SIMPLEX_ABS_LIM) / (2 * SIMPLEX_ABS_LIM)
    program {
        val rpx = RealPx(this, 0.0, 0.0, 100.0)
        val heightField = { v: Vector2 ->
            unitNoise(v * 0.4) - abs(unitNoise(v * 0.1, seed + 1)) * 0.4
        }.toScalarField()
        val shadowVector: Vector3 = Vector3(-2.0, 1.0, 0.2)
        val realBounds = run {
            val (x1, y1) = rpx.fromPx(0, 0)
            val (x2, y2) = rpx.fromPx(width, height)
            Rectangle(x1, -y1, abs(x2 - x1), abs(y2 - y1))
        }

        val cb = colorBuffer(width, height)
        cb.shadow.let { img ->
            for (y in 0 until height) {
                for (x in 0 until width) {
                    rpx.withReal(Vector2(x.d, y.d)) { v ->
                        if (x == 50 && y == 50) {
                            println("hehe v=$v and ${v in realBounds}")
                            println("real bounds ${realBounds.corner} w=${realBounds.width} h=${realBounds.height}")
                        }

                        val h = heightField(v)
                        val isShadowed = generateSequence(v) {
                            it + shadowVector.xy * 0.01 // resolution (!)
                        }.drop(1)
                            .takeWhile { it in realBounds }
                            .any {
                                (it - v).length * shadowVector.z < heightField(it) - h
                            }

                        val intensity: Double = heightField.normalVector(v).normalized.dot(shadowVector.normalized)
                        if (isShadowed) {
                            img[x, y] = ColorRGBa.ORANGE.shade(intensity * 0.1 + 0.3)
                        } else {
                            img[x, y] = ColorRGBa.ORANGE.shade(intensity * 0.5 + 0.5)
                        }
                    }
                }
            }
            img.upload()
        }
        println("done")
        extend {
            drawer.image(cb)
        }
    }
}