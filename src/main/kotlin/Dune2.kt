import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.renderTarget
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.color.presets.DARK_ORANGE
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import util.RealPx
import util.SF
import util.normalAt
import kotlin.math.min
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1080
        height = 720
    }
    program {
        val seed = Random.nextInt()
        val rpx = RealPx(this, 0.0, 0.0, 400.0)

        val heightField: SF = { at ->
            val s1 = simplex(seed, at).normalized()
            val s2 = simplex(seed + 1, at).normalized()
            min(s1, s2)
        }

        val rt = renderTarget(width, height) {
            colorBuffer()
        }
        val cb = rt.colorBuffer(0)

        val dirStep = Vector3(0.01, 0.0, 0.02) / 10.0

        cb.shadowContext { img ->
            for (x in 0 until rt.width) {
                for (y in 0 until rt.height) {
                    rpx.withReal(Vector2(x.d, y.d)) { v ->
                        val isLit = run {
                            var curPos = Vector3(v.x, v.y, heightField(v))
                            while (curPos.z < SIMPLEX_ABS_LIM) {
                                curPos += dirStep
                                if (heightField(curPos.xy) > curPos.z) {
//                                    val (j, i) = rpx.fromReal(curPos.xy)
//                                    img[j.toInt(), i.toInt()] = ColorRGBa.RED
                                    return@run false
                                }
                            }
                            return@run true
                        }
                        val ambientValue = 0.3
                        val maxValue = 2.0
                        if (isLit) {
                            val intensity = run {
                                val normal = heightField.normalAt(v).normalized
                                val lightDir = dirStep.normalized
                                val viewDir = Vector3.UNIT_Z
                                val lightingAngCos = normal.dot(lightDir)
                                val viewingAngCos = normal.dot(viewDir)
                                return@run lightingAngCos * viewingAngCos
                            }
                            val shadeCoef = ambientValue + intensity * (maxValue - ambientValue)
                            img[x, y] = ColorRGBa.DARK_ORANGE.shade(
                                shadeCoef.coerceAtMost(1.0)
                            )
                        } else {
                            img[x, y] = ColorRGBa.DARK_ORANGE.shade(ambientValue)
                        }
                    }
                }
            }
        }

        println("drawing")
        extend {
            drawer.image(cb)
        }
    }
}

