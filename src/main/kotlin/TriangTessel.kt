import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.extra.compositor.blend
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.fx.blend.Overlay
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.math.Vector4
import org.openrndr.shape.Triangle
import kotlin.random.Random

fun main() = application {
    configure {
        width - 800
        height = 600
    }
    val random = Random
    program {
        val imageFilename = "data/images/cheeta.jpg"
        val image = loadImage(imageFilename)
        val shadow = image.shadow
        shadow.download()

        fun tesselify(basePointsCount: Int, sampleCount: Int, random: Random): List<Pair<Triangle, ColorRGBa>> {
            val bound = image.bounds
            val delaunayPoints = List(basePointsCount) { Random.point(bound) } + bound.contour.segments.map { it.start }
            val triangles = Delaunay.from(delaunayPoints).triangles()

            val avgColors = triangles.map { triangle ->
                val (r, g, b, a) = (List(sampleCount) { random.point(triangle) }
                    .map { shadow[it.x.toInt(), it.y.toInt()].toVector4() }
                    .reduce(Vector4::plus) / sampleCount.d)
                ColorRGBa(r, g, b, a)
            }
            return triangles zip avgColors
        }

        val layerCount = 10
        val layersData = List(layerCount) { tesselify(50, 300, random) }
        println("precalc done")

        val composite = compose {
            repeat(layerCount) { i ->
                layer {
                    val tess = layersData[i]
                    blend(Overlay())
                    draw {
                        drawer.apply {
                            for ((triangle, color) in tess) {
                                strokeWeight = 0.0
//                                stroke = null
                                fill = color // sqrt(layerCount.d)
                                shape(triangle.shape)
                            }
                        }
                    }
                }
            }
        }

        extend {
            composite.draw(drawer)
        }
    }
}