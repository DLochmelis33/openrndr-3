import org.openrndr.Fullscreen
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
        width = 1920
        height = 1080
//        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    println(Vector2(1.0, 1.0).decompose(Vector2(2.0, 0.0), Vector2(1.0, -1.0)))
    program {
        val controlPoints = mutableListOf(
            Vector2(width * 0.2, height * 0.5),
            Vector2(width * 0.4, height * 0.5),
            Vector2(width * 0.6, height * 0.5),
            Vector2(width * 0.8, height * 0.5)
        )
        val controlPointRadius = 10.0

        val fract: Fract = object : Fract {
            override fun fract(seg: Segment): List<Segment> {
                val pb1 = controlPoints.last() - controlPoints.first()
                val pb2 = -Vector2(-pb1.y, pb1.x)
                val vb1 = seg.end - seg.start
                val vb2 = -Vector2(-vb1.y, vb1.x)

                val r = controlPoints.zipWithNext { pi, pj ->
                    val (aj, bj) = (pj - pi).decompose(pb1, pb2)
                    val rj = vb1 * aj + vb2 * bj
                    rj
                }
//                return (r + seg.end).zipWithNext { a, b -> Segment(seg.start + a, seg.start + b) }
                return r.fold(listOf(seg.start)) { acc, rj -> acc + (rj + acc.last()) }
                    .zipWithNext(::Segment)
            }
        }

        lateinit var segs: List<Segment>
        fun calc() {
            rs = makeRs()
//            segs = listOf(Segment(controlPoints[0], controlPoints[1]))
            segs = controlPoints.zipWithNext { a, b -> Segment(a, b) }
            repeat(7) {
                segs = segs.flatMap { fract.fract(it) }
            }
        }
        calc()
        var tgtIdx: Int? = null
        mouse.buttonDown.listen {
            for (i in controlPoints.indices) {
                if ((it.position - controlPoints[i]).length < controlPointRadius) tgtIdx = i
            }
        }
        mouse.buttonUp.listen {
            tgtIdx = null
            calc()
        }
        mouse.dragged.listen {
            tgtIdx?.let { i ->
                controlPoints[i] = it.position
            }
        }
        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                fill = ColorRGBa.BLUE
                for (s in segs) segment(s)
                for (p in controlPoints) circle(p, controlPointRadius)
            }
        }
    }
}
