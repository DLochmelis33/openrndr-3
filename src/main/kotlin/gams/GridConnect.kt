package gams

import d
import org.openrndr.MouseEvent
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extras.color.presets.LIGHT_GRAY
import org.openrndr.math.Vector2
import kotlin.math.sqrt

fun main() = application {
    configure {
        width = 1200
        height = 800
    }
    program {
        lateinit var dots: List<Vector2>
        val connections = mutableListOf<Pair<Int, Int>>() // indices in dots

        val gui = GUI()
        val settings = object {
            @IntParameter("grid width", 1, 7)
            var gw = 3

            @IntParameter("grid height", 1, 7)
            var gh = 3

            @ActionParameter("reset")
            fun reset() {
                val unit = height * 0.5 / sqrt(gh.d * gw.d)
                dots = List(gh) { i ->
                    List(gw) { j ->
                        val c = height * 0.33
                        Vector2(c + j * unit, c + i * unit)
                    }
                }.flatten()
                connections.clear()
            }
        }
        gui.add(settings, "settings")
        extend(gui)

        fun closeDot(e: MouseEvent) = dots.indexOfFirst { it.distanceTo(e.position) < 10.0 }.takeUnless { it == -1 }
        fun isConnected(idx: Int) = connections.any { (a, b) -> a == idx || b == idx }

        var connectingFromIdx: Int? = null
        mouse.dragged.listen {
            connectingFromIdx?.let { last ->
                closeDot(it)?.let { cur ->
                    if (!isConnected(cur) && cur != last) {
                        connections.add(last to cur)
                        connectingFromIdx = cur
                    }
                }
            }
        }
        mouse.buttonDown.listen {
            closeDot(it)?.let {
                if (!isConnected(it)) connectingFromIdx = it
            }
        }
        mouse.buttonUp.listen {
            connectingFromIdx = null
        }

        settings.reset()

        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                for ((i, v) in dots.withIndex()) {
                    if (isConnected(i)) {
                        fill = ColorRGBa.PINK
                        circle(v, 10.0)
                    } else {
                        fill = ColorRGBa.LIGHT_GRAY
                        circle(v, 3.0)
                    }
                }
                strokeWeight = 3.0
                for ((a, b) in connections) {
                    lineSegment(dots[a], dots[b])
                }
                connectingFromIdx?.let {
                    lineSegment(dots[it], mouse.position)
                }
            }
        }
    }
}