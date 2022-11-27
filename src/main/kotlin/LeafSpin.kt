import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.math.Spherical
import org.openrndr.math.Vector2
import org.openrndr.shape.contour

fun main() = application {
    configure {
        width = 800
        height = 600
    }
    program {

        val composite = compose {

            draw {
                val sphereRadius = 200.0
                val basisUnit = 30.0
                val theta = seconds * 50.0
                val phi = seconds * 20.0

                val stem = Spherical(theta, phi, sphereRadius).cartesian
                val a = Spherical(theta + 90.0, 90.0, basisUnit).cartesian
                val b = Spherical(theta, phi + 90, basisUnit).cartesian

                val squareCorners = listOf(
                    a + b,
                    a - b,
                    -a - b,
                    -a + b
                ).map { it.xy }

                val projSquare = contour {
                    moveTo(squareCorners[0])
                    for (i in 1..3) lineTo(squareCorners[i])
                    close()
                }

                drawer.apply {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    translate(width / 2.0, height / 2.0)
                    lineSegment(Vector2.ZERO, stem.xy)
                    translate(stem.xy)
                    contour(projSquare)
                }
            }

            post(GaussianBloom()) {
                sigma = 2.0
                passes = 2
                noiseGain = 0.1
                gain = 2.0
                window = 10
            }
        }

        extend {
            composite.draw(drawer)
        }
    }
}