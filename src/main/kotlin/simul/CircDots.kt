package simul

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import kotlin.time.Duration.Companion.microseconds

fun main() = application {
    program {
        Random.seed = kotlin.random.Random.nextInt().toString()
        val angles = MutableList(10) { Random.double0(360.0) }
        fun step() {
            val i = Random.int0(angles.size)
            val j = Random.int0(angles.size)
            val w = 0.999
            angles[i] = (angles[i] * w + angles[j] * (1.0 - w)) % 360.0
        }
//        keyboard.keyDown.listen {
//            if (it.key == KEY_SPACEBAR) {
//                step()
//            }
//        }
        GlobalScope.launch {
            while (true) {
                step()
                delay(1.microseconds)
            }
        }
        extend {
            drawer.isolated {
                clear(ColorRGBa.WHITE)
                translate(drawer.bounds.center)
                val radius = 100.0
                fill = null
                circle(0.0, 0.0, radius)
                fill = ColorRGBa.PINK
                for (a in angles) {
                    isolated {
                        rotate(a)
                        translate(radius, 0.0)
                        circle(0.0, 0.0, 10.0)
                    }
                }
//                step()
            }
        }
    }
}