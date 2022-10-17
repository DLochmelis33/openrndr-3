package util

import org.openrndr.draw.*
import kotlin.math.roundToInt

const val widthUHD = 3840
const val heightUHD = 2160

//fun Drawer.multisampling(sampleCount: Int, function: Drawer.() -> Unit) {
//    val rt = renderTarget(width, height, multisample = BufferMultisample.SampleCount(sampleCount)) {
//        colorBuffer()
//        depthBuffer()
//    }
//    isolatedWithTarget(rt) {
//        function()
//    }
//    val resolved = colorBuffer(width, height)
//    rt.colorBuffer(0).copyTo(resolved)
//    image(resolved)
//}
//
//fun Drawer.customUpscaled(upscale: Double, function: Drawer.() -> Unit) = isolated {
//    val uw = (width * upscale).roundToInt()
//    val uh = (height * upscale).roundToInt()
//    val rt = renderTarget(uw, uh) {
//        colorBuffer()
//        depthBuffer()
//    }
//    isolatedWithTarget(rt) {
//        function()
//    }
//    scale(1.0 / upscale)
//    image(rt.colorBuffer(0))
//}
