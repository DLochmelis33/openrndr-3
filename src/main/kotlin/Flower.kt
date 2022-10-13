import kotlinx.coroutines.delay
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extras.color.presets.LIGHT_PINK
import org.openrndr.launch
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Segment
import kotlin.random.Random

private val Double.cu: Double get() = this

private data class GenParams(
    val branchingDistribution: List<Pair<Double, Int>>,
    val angleVariance: ClosedFloatingPointRange<Double>,
    val lengthVariance: ClosedRange<Double>,
    val blossomProbability: Double,
    val blossomSizeVariance: ClosedRange<Double>,
)

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    val random = Random

    val bgColor = ColorRGBa(0.9, 0.9, 0.9)
    val branchColor = ColorRGBa(0.3, 0.2, 0.2)
    val blossomColor = ColorRGBa.PINK
//    val blossomColor = ColorRGBa(212 / 255.0, 74 / 255.0, 47 / 255.0)

    fun getBranchingDistribution(stage: Int) = listOf(
        0..2 to GenParams(
            listOf(
                1.0 to 1,
                2.0 to 2,
                1.0 to 3,
            ),
            -0.3..0.3,
            30.0.cu..50.0.cu,
            0.0,
            0.0.cu..0.0.cu,
        ),
        3..8 to GenParams(
            listOf(
                1.0 to 0,
                3.0 to 1,
                2.0 to 2,
                2.0 to 3,
            ),
            -50.0..50.0,
            20.0.cu..50.0.cu,
            0.3,
            3.0.cu..7.0.cu,
        ),
        9..50 to GenParams(
            listOf(
                2.0 to 0,
                1.0 to 1,
                1.0 to 2,
            ),
            -30.0..30.0,
            5.0.cu..30.0.cu,
            0.4,
            2.0.cu..6.0.cu,
        )
    ).first { stage in it.first }.second


    program {
//        val start = Vector2(width / 2.0, height * 0.8)

        val resultingSegments = mutableListOf<Segment>()
        val resultingBlossoms = mutableListOf<Circle>()

        suspend fun generate(start: Vector2, clear: Boolean = true) {
            if (clear) {
                resultingSegments.clear()
                resultingBlossoms.clear()
            }
            var stage = 0
            var currentBranches: List<Pair<Vector2, Double>> = listOf(start to 270.0)
            while (currentBranches.isNotEmpty()) {
                currentBranches = currentBranches.flatMap { (point, ang) ->
                    val (
                        branchingDistribution,
                        angleVariance,
                        lengthVariance,
                        blossomProbability,
                        blossomSizeVariance)
                            = getBranchingDistribution(stage)
                    val newBranchesCount = random.weightedChoice(branchingDistribution)
                    List(newBranchesCount) {
                        val nextAng = when (ang) {
                            in 20.0..160.0 -> random.nextDouble(180.0..360.0)
                            else -> ang + random.nextDouble(angleVariance)
                        } % 360.0
                        val nextLen = random.nextDouble(lengthVariance)
                        val nextSegment = Segment(point, point + Polar(nextAng, nextLen).cartesian)
                        resultingSegments.add(nextSegment)
                        if (random.coinflip(blossomProbability)) {
                            resultingBlossoms.add(
                                Circle(
                                    nextSegment.position(random.nextDouble()),
                                    random.nextDouble(blossomSizeVariance)
                                )
                            )
                        }
                        nextSegment.end to nextAng
                    }
                }.filterNot { (cp, _) ->
                    currentBranches.map { it.first }.any { sp ->
                        cp.distanceTo(sp) < 15.0.cu
                    }
                }
                stage++
                delay(100)
            }
        }

        keyboard.keyDown.listen {
            when (it.key) {
                KEY_SPACEBAR -> launch {
                    generate(Vector2(width * 0.5, height * 0.9))
//                    generate(Vector2(width * 0.7, height * 0.9), false)
                }
                else -> Unit
            }
        }

        extend {
            drawer.run {
                clear(bgColor)
                stroke = branchColor
                fill = branchColor
                segments(resultingSegments)
                stroke = null
                fill = blossomColor
                circles(resultingBlossoms)
            }
        }
    }
}

