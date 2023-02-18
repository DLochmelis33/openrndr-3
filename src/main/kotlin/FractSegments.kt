import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import kotlin.math.sqrt

interface Fract {
    fun fract(seg: Segment): List<Segment>
}

val koch = object : Fract {
    private val lenCoef = 1.0 / (2.0 + sqrt(2.0))
    override fun fract(seg: Segment): List<Segment> = seg.run {
        val coef = 1.0 - sqrt(2.0) / 2.0
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

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val fract: Fract = koch
        val initial = Segment(Vector2(width * 0.2, height * 0.3), Vector2(width * 0.8, height * 0.7))
        var segs = listOf(initial)
        repeat(5) {
            segs = segs.flatMap { fract.fract(it) }
        }
        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                for (s in segs) segment(s)
            }
        }
    }
}
