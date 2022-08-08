import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import java.lang.Math.acos
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.reflect.KProperty

fun Random.weightedIndex(weights: List<Int>): Int {
    var t = nextInt(weights.sum())
    var i = 0
    while (i < weights.size && t >= 0) {
        t -= weights[i]
        i++
    }
    return i - 1
}

val Int.d get() = toDouble()

fun Segment.randomPoint(from: Double = 0.0, to: Double = 1.0, random: Random = Random) =
    start + (end - start) * random.nextDouble(from, to)

fun <T> List<T>.zipNextCyclic() = zip(subList(1, size) + first())

fun Vector2.ang(other: Vector2) = acos(dot(other) / (length * other.length))

private const val h = 1e-6
fun ((Double) -> Double).dif(): (Double) -> Double = { (this(it + h) - this(it - h)) / (2 * h) }

operator fun <V> (() -> V).getValue(thisRef: Any?, property: KProperty<*>): V {
    return this()
}

fun Random.nextDouble(range: ClosedRange<Double>) = nextDouble(range.start, range.endInclusive)

fun timestamp(): String {
    val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd_HH-mm-ss-SSS")
    return LocalDateTime.now().format(formatter)
}
