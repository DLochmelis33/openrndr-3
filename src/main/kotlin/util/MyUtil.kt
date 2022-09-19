import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import java.lang.Math.acos
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
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

fun Random.vec2(scale: Double) = Vector2(nextDouble(-scale, scale), nextDouble(-scale, scale))

val Int.d get() = toDouble()

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

// wraparound get
operator fun <T> Array<T>.invoke(i: Int) = this[(i + size) % size]
operator fun <T> List<T>.invoke(i: Int) = this[(i + size) % size]

fun <T> List<T>.rotate(shift: Int): List<T> {
    val posShift = (if (shift < 0) shift + size * (abs(shift) / size + 1) else shift) % size
    return subList(posShift, size) + subList(0, posShift)
}

operator fun <T> Array<Array<T>>.get(coords: Pair<Int, Int>): T = this[coords.first][coords.second]
operator fun <T> Array<Array<T>>.set(coords: Pair<Int, Int>, value: T) {
    this[coords.first][coords.second] = value
}

inline fun <reified T> Array<Array<T>>.deepCopy(): Array<Array<T>> = this.map { it.copyOf() }.toTypedArray()

// flat!
operator fun <T, U> List<T>.times(other: List<U>) = flatMap { t -> other.map { u -> Pair(t, u) } }

fun makeGrid(width: Int, height: Int, rows: Int, cols: Int) = List(rows) { i ->
    List(cols) { j ->
        Vector2(j * width.d / cols, i * height.d / rows)
    }
}

fun Program.makeGrid(rows: Int, cols: Int) = makeGrid(width, height, rows, cols)

interface RandomScope {
    val rng: Double
    val nrng: Double
    val r: Random
}

fun <T> randomScope(seed: Long = 5, block: RandomScope.() -> T): T {
    val rand = Random(seed)
    return object : RandomScope {
        override val r = rand
        override val rng get() = rand.nextDouble()
        override val nrng get() = rand.nextDouble(-1.0, 1.0)
    }.block()
}

fun Random.nextRGB() = ColorRGBa(nextDouble(), nextDouble(), nextDouble())
