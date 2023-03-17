import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.triangulation.Delaunay
import kotlin.time.Duration.Companion.milliseconds

fun main() = application {
    configure {
        width = 1920
        height = 1080
    }
    Random.randomizeSeed()
    program {
        val initPts = List(1000) { Random.point(drawer.bounds) }
        val triangles = Delaunay.from(initPts).triangles()
        val centers = triangles.map { it.centroid }
        val graph = List(centers.size) { mutableSetOf<Int>() } // map on indices
        triangles
            .flatMapIndexed { i, t -> t.contour.segments.map { it to i } }
            .groupBy({ (it.first.start + it.first.end) / 2.0 }, { it.second })
            .values.filter { it.size == 2 }
            .forEach { (i, j) ->
                graph[i].add(j)
                graph[j].add(i)
            }
        val colors = triangles.map { ColorRGBa.BLACK }.toMutableList()
        fun randomColor() = ColorHSVa(Random.double0(360.0), 1.0, 0.7).toRGBa()
        val walkers = MutableList(5) { Random.int0(graph.size) to randomColor() }
        walkers.indices.forEach { t ->
            GlobalScope.launch {
                while (true) {
                    val (i, c) = walkers[t]
                    val j = run {
                        val options = graph[i].filterNot { colors[it] == c }
                        if (options.isEmpty()) graph[i].random() else options.random()
                    }
                    val nc = c // if (colors[j] != c && colors[j] != ColorRGBa.BLACK) randomColor() else c
                    colors[j] = c
                    walkers[t] = j to nc
                    delay(10.milliseconds)
                }
            }
        }
        extend {
            drawer.isolated {
                for (i in graph.indices) {
                    fill = colors[i]
                    shape(triangles[i].shape)
                }
            }
        }
    }
}