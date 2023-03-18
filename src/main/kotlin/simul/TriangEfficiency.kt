package simul

import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.extra.noise.Random
import org.openrndr.shape.Triangle
import util.triangles

fun main() = application {
    configure {
        width = 1200
        height = 800
    }
    program {
        val n = 1000
        val colors = List(n) { ColorHSVa(Random.double0(360.0), 1.0, 1.0, 0.01).toRGBa() }
        extend {
            val triangles = List(n) {
                Triangle(
                    Random.point(drawer.bounds),
                    Random.point(drawer.bounds),
                    Random.point(drawer.bounds)
                )
            }
            drawer.triangles(triangles, colors)
        }
    }
}