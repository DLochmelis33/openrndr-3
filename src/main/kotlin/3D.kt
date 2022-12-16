import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extras.meshgenerators.boxMesh
import org.openrndr.math.Vector3

fun main() = application {
    configure {
        width = 1200
        height = 600
        multisample = WindowMultisample.SampleCount(4)
    }
    program {
        val cube = boxMesh(50.0, 50.0, 50.0)

        var freeze = false
        var curSeconds = 0.0
        fun mySeconds() = if (freeze) curSeconds else seconds

        keyboard.keyDown.listen {
            curSeconds = seconds
            freeze = !freeze
        }

        fun Drawer.drawCube(xShift: Double) = isolated {
            perspective(60.0, width * 1.0 / height, 0.01, 1000.0)
            depthWrite = true
            depthTestPass = DepthTestPass.LESS_OR_EQUAL

            fill = ColorRGBa.PINK
            shadeStyle = shadeStyle {
                fragmentTransform = """
                        vec3 lightDir = normalize(vec3(0.3, 1.0, 0.5));
                        float l = dot(va_normal, lightDir) * 0.4 + 0.5;
                        x_fill.rgb *= l; 
                    """.trimIndent()
            }
            translate(xShift, 0.0, -150.0)
            rotate(Vector3.UNIT_X, mySeconds() * 15 + 30)
            rotate(Vector3.UNIT_Y, mySeconds() * 5 + 60)
            vertexBuffer(cube, DrawPrimitive.TRIANGLES)
        }

        val xDiff = -30.0
        val xOffset = 0.0
        extend {
            drawer.run {
                translate(xOffset, 0.0)
                drawCube(-xDiff)
                translate(-2 * xOffset, 0.0)
                drawCube(xDiff)
            }
        }
    }
}