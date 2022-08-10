import org.openrndr.KEY_ENTER
import org.openrndr.KEY_ESCAPE
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.BufferMultisample
import org.openrndr.draw.Drawer
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.renderTarget
import org.openrndr.extras.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Shape
import org.openrndr.shape.shape
import util.*
import java.io.File
import kotlin.math.*
import kotlin.random.Random
import kotlin.system.exitProcess

fun main() = application {
    configure {
        width = 1200
        height = 700
    }
    val r = Random

    fun genRandomWave(yShift: Double): (Double) -> Double {
        val randomValues = mutableListOf<Double>()
        var pointer = 0

        fun rng(from: Double = 0.0, until: Double = 1.0): Double {
            if (randomValues.size == pointer) randomValues.add(r.nextDouble(from, until))
            return randomValues[pointer].also { pointer++ }
        }

        fun _rng() = rng(0.0, 1.0)
        val rng by ::_rng
        fun _nrng() = rng(-1.0, 1.0)
        val nrng by ::_nrng

        val f = { x: Double ->
            (+nrng * sin(nrng * x)
                    + nrng * sin(nrng * x) * cos(nrng * x + nrng * 2.0)
                    + 0.2 * nrng * x
                    + yShift
                    ).also { pointer = 0 }
        }

        pointer = 0
        return f
    }

    program {
        val converter = RealPx(this, 0.0, 0.0, width / (5.0 * 2.0))
        val evalRealPts = ((-10)..(width + 10) step 5).map { converter.pxToReal(it, height / 2.0).toVector2() }

        fun constructShape(f: (Double) -> Double): Shape {
            val calc = { real: Vector2 -> converter.realToPx(real.x, f(real.x)).toVector2() }
            return shape {
                contour {
                    moveTo(0.0, height.d)
                    lineTo(calc(evalRealPts.first()))
                    for (p in evalRealPts.drop(1)) {
                        continueTo(calc(p))
                    }
                    lineTo(width.d, height.d)
                    close()
                }
            }
        }

        val fromRy = converter.pxToReal(0, height * (1.0)).second
        val untilRy = converter.pxToReal(0, height * (-0.2)).second

        var shapeCount = 10
        lateinit var shapes: List<Shape>

        lateinit var hills1: ColorRGBa
        lateinit var hills2: ColorRGBa

        fun generate() {
            shapeCount = r.nextInt(4, 10)
            shapes = List(shapeCount) {
                constructShape(genRandomWave(fromRy + (it.d / shapeCount) * (untilRy - fromRy)))
            }
            do {
                hills1 = randomPastel(r).toRGBa()
                hills2 = randomPastel(r).toRGBa()
                val hueDelta = abs(hills1.toHSVa().h - hills2.toHSVa().h)
            } while (hueDelta < 60.0)
        }

        fun Drawer.doDraw() {
            backgroundColor = hills2
//                strokeWeight = 1.0
            stroke = null

            for ((i, shape) in shapes.withIndex().reversed()) {
                fill = hills1.mix(hills2, i.d / shapeCount)
                shape(shape)
            }
        }

        fun save() {
            println("saving...")
            val widthUHD = 3840
            val heightUHD = 2160
            val rt = renderTarget(widthUHD, heightUHD, multisample = BufferMultisample.SampleCount(8)) {
                colorBuffer()
                depthBuffer()
            }
            drawer.withTarget(rt) {
                drawer.doDraw()
            }
            val resolved = colorBuffer(widthUHD, heightUHD)
            rt.colorBuffer(0).copyTo(resolved)
            resolved.saveToFile(File("saves/hills_${timestamp()}.png"))
            println("saved")
        }

        keyboard.keyDown.listen { event ->
            when (event.key) {
                KEY_SPACEBAR -> generate()
                KEY_ESCAPE -> exitProcess(0)
                KEY_ENTER -> save()
            }
        }
        generate()

        extend {
            drawer.doDraw()
        }
    }
}
