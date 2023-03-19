import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.shapes.grid
import org.openrndr.extras.color.presets.BLUE_STEEL
import org.openrndr.math.Matrix44
import org.openrndr.math.transforms.transform
import org.openrndr.shape.Rectangle
import kotlin.math.PI
import kotlin.math.sqrt

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    program {
        Random.seed = kotlin.random.Random.nextInt().toString()
        val w = width / 10
        val h = height / 10
        val grid = drawer.bounds.grid(w, h)

        val simplexSeed = Random.int()
        val cyclePeriod = 30.0 // seconds
        val changeSpeed = 4.0
        val fillThresh = 0.65 // from -1 to 1
        val fillFreq = 1.7 // at least 1.0
        val noiseScale = 1.5

//        val recorder = ScreenRecorder().apply {
//            maximumDuration = cyclePeriod
//            quitAfterMaximum = true
//        }
//        extend(recorder)

        fun isColored(x: Double, y: Double): Boolean {
            val t = seconds * 2.0 * PI / cyclePeriod
            return (simplex(
                simplexSeed, x, y,
                seconds / cyclePeriod * changeSpeed
                // sin(t) * changeSpeed, cos(t) * changeSpeed
            ) / SIMPLEX_ABS_LIM * fillFreq) % 1.0 > fillThresh
        }

        fun isColored2(x: Double, y: Double): Boolean {
            val t = seconds * 2.0 * PI / cyclePeriod
            return (simplex(
                simplexSeed + 5, x, y,
                seconds / cyclePeriod * changeSpeed * 0.5
                // sin(t) * changeSpeed, cos(t) * changeSpeed
            ) / SIMPLEX_ABS_LIM * fillFreq * 0.7) % 1.0 > fillThresh + 0.2
        }

        extend {
            val colored = mutableListOf<Rectangle>()
            val colored2 = mutableListOf<Rectangle>()

            val s = sqrt(w.d * h.d)
            for (xi in 0 until w / 2) {
                for (yi in 0 until h / 2) {
                    val x = xi / s * noiseScale
                    val y = yi / s * noiseScale
                    if (isColored(x, y)) colored.add(grid[yi][xi])
                    if (isColored2(x, y)) colored2.add(grid[yi][xi])
                }
            }

            drawer.isolated {
                stroke = null

                fun doDraw() = isolated {
//                    fill = ColorRGBa.BLUE_STEEL
                    fill = ColorRGBa.PINK
                    rectangles(colored)
                    rectangles(colored2)
                }

                val mFlipX = Matrix44.IDENTITY.copy(c0r0 = -1.0)
                val mFlipY = Matrix44.IDENTITY.copy(c1r1 = -1.0)

                doDraw()
                isolated {
                    drawer.model *= transform(mFlipX) {
                        translate(-width.d, 0.0)
                    }
                    doDraw()
                }
                isolated {
                    drawer.model *= transform(mFlipY) {
                        translate(0.0, -height.d)
                    }
                    doDraw()
                }
                isolated {
                    drawer.model *= transform(mFlipX * mFlipY) {
                        translate(-width.d, -height.d)
                    }
                    doDraw()
                }
            }
        }
    }
}
