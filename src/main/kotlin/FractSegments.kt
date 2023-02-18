import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import kotlin.math.sqrt
import kotlin.random.Random

interface Fract {
    fun fract(seg: Segment): List<Segment>
}

val koch = object : Fract {
    private val coef = 1.0 / (2.0 + sqrt(2.0))
    override fun fract(seg: Segment): List<Segment> = seg.run {
        val base1 = (end - start) * coef
        val base2 = -Vector2(-base1.y, base1.x)

        val p0 = start
        val p1 = p0 + base1
        val p2 = p1 + (base1 + base2) * sqrt(2.0) / 2.0
        val p3 = p1 + base1 * sqrt(2.0)
        val p4 = end
        listOf(Segment(p0, p1), Segment(p1, p2), Segment(p2, p3), Segment(p3, p4))
    }
}

val random = Random
fun makeRs() = generateSequence { random.nextDouble(0.0, 2.0) }.take(100).toList()
var rs = makeRs()

val hehe = object : Fract {

    override fun fract(seg: Segment): List<Segment> = seg.run {
        val base1 = (end - start) / 4.0
        val base2 = -Vector2(-base1.y, base1.x)

        val iter = rs.iterator()
        fun r() = iter.next()

        val p0 = start
        val p1 = p0 + base1 * r() - base2 * r()
        val p2 = p0 + base1 * (2.0 + r()) + base2 * r()
        val p3 = end

        listOf(p0, p1, p2, p3).zipWithNext { a, b -> Segment(a, b) }
    }
}

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val fract: Fract = hehe
        val initial = Segment(Vector2(width * 0.2, height * 0.5), Vector2(width * 0.8, height * 0.5))
        lateinit var segs: List<Segment>
        fun calc() {
            rs = makeRs()
            segs = listOf(initial)
            repeat(8) {
                segs = segs.flatMap { fract.fract(it) }
            }
        }
        calc()
        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                calc()
            }
        }
        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                for (s in segs) segment(s)
//                segment(initial)
            }
        }
    }
}
