import org.openrndr.KEY_ENTER
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import util.saveIsolatedMultisample
import kotlin.math.pow

interface Fract {
    fun fract(seg: Segment): List<Segment>
}

fun main() = application {
    configure {
        width = 1920
        height = 1080
//        fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    program {
        lateinit var segs: List<Segment>
        lateinit var controlPoints: MutableList<Vector2>
        val controlPointRadius = 10.0

        val gui = GUI()
        val settings = object {
            @IntParameter("number of control points", 2, 10)
            var controlPointsCount = 5

            @IntParameter("recursion depth", 0, 10)
            var calcDepth = 6

            @ColorParameter("background color", 100)
            var backgroundColor = ColorRGBa.PINK

            @ColorParameter("main color", 101)
            var mainColor = ColorRGBa.BLACK

            @DoubleParameter("line width", 0.001, 1.0)
            var lineWidth = 1.0

            @ActionParameter("reset and calc")
            fun resetAndCalc() {
                controlPoints = MutableList(controlPointsCount) {
                    Vector2(width * 0.2 + (width * 0.6 * it / (controlPointsCount - 1)), height * 0.5)
                }
                calc()
            }

            @ActionParameter("save 4k screenshot")
            fun save() = drawer.saveIsolatedMultisample("fract") {
                clear(backgroundColor)
                stroke = mainColor
                strokeWeight = lineWidth
                segments(segs)
            }

            @ActionParameter("force calc without reset")
            fun calc() {
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
                        return r.fold(listOf(seg.start)) { acc, rj -> acc + (rj + acc.last()) }
                            .zipWithNext(::Segment)
                    }
                }
                segs = controlPoints.zipWithNext { a, b -> Segment(a, b) }
                repeat(calcDepth) {
                    segs = segs.flatMap { fract.fract(it) }
                }
            }
        }
        gui.add(settings, "settings")
        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                gui.visible = !gui.visible
            }
        }
        extend(gui)

        settings.resetAndCalc()
        settings.calc()
        var tgtIdx: Int? = null
        mouse.buttonDown.listen {
            for (i in controlPoints.indices) {
                if ((it.position - controlPoints[i]).length < controlPointRadius) tgtIdx = i
            }
        }
        mouse.buttonUp.listen {
            tgtIdx = null
        }
        mouse.dragged.listen {
            tgtIdx?.let { i ->
                controlPoints[i] = it.position
                settings.calc()
            }
        }
        extend {
            drawer.isolated {
                clear(settings.backgroundColor)
                stroke = settings.mainColor
                strokeWeight = settings.lineWidth
                segments(segs)

                for ((i, p) in controlPoints.withIndex()) {
                    fill = ColorRGBa.BLUE.mix(ColorRGBa.RED, i / (controlPoints.size - 1.0))
                    circle(p, controlPointRadius)
                }
            }
        }
    }
}
