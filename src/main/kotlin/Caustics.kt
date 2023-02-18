import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.simplex2D
import org.openrndr.extras.color.presets.ORANGE
import org.openrndr.math.Vector2
import kotlin.math.floor
import kotlin.math.roundToInt

private const val EPS = 1e-6

fun squish(points: List<Double>, bucketCount: Int): List<Int> {
//    val (min, max) = min(points) to max(points)
    val (min, max) = -SIMPLEX_ABS_LIM to SIMPLEX_ABS_LIM
    val step = (max - min) / bucketCount
    val buckets = MutableList(bucketCount) { 0 }
    fun getBucketIdx(value: Double) = if (value == max) bucketCount - 1 else floor((value - min) / step).toInt()
    for (point in points) buckets[getBucketIdx(point)]++
    return buckets
}

fun main() = application {
    configure {
        width = 1920
        height = 1080
        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    val seed = (System.currentTimeMillis() * this.hashCode()).toInt()
//    val seed: Int = 5

    program {
        val bgColor = ColorRGBa.WHITE * 0.05
        val baseColor = ColorRGBa.ORANGE.opacify(0.9)

        suspend fun generate(width: Int, height: Int): Map<Int, List<Vector2>> {
            val sampleCount = 50000
            val sampleStep = 1.0 / 10000.0
//            val xRange = (width * 0 / 10)..(width * 10 / 10)
            val xRange = 0..width
            val xScale = 2.0 / (xRange.last - xRange.first)
            val yRange = (height * 2 / 10)..(height * 8 / 10)
            val brightnessCoef = 0.004 // obscurely depends on ALL other parameters; modify manually often

            val bCount = yRange.last - yRange.first
            val zPoints = List(sampleCount) { it * sampleStep }

            fun calcColorGroup(count: Int): Int {
                val v = count * brightnessCoef
                return when {
                    v < EPS -> 0
                    v >= 1.0 -> 255
                    else -> (v * 255).roundToInt()
                }
            }

            val xToBuckets = coroutineScope {
                xRange.map { x ->
                    async {
                        val points = zPoints.map { simplex2D(seed, x.d * xScale, it) }
                        squish(points, bCount)
                    }
                }.awaitAll()
            }

            val res = coroutineScope {
                (xRange zip xToBuckets).map { (x, hist) ->
                    async {
                        hist.withIndex().map { (i, count) ->
                            val y = yRange.first + i.d / hist.size * (yRange.last - yRange.first)
                            Vector2(x.d, y) to calcColorGroup(count)
                        }
                    }
                }.awaitAll().flatten()
            }

            val circlesAssoc = res.groupBy({ it.second }) { it.first }
            return circlesAssoc
        }

        val circlesAssoc = generate(width, height)
        extend {
            drawer.isolated {
                clear(bgColor)
                for ((colorGroup, circles) in circlesAssoc) {
                    fill = baseColor * (colorGroup / 255.0)
                    stroke = null
                    rectangles(circles, 1.0, 1.0)
                }
            }
        }
    }
}
