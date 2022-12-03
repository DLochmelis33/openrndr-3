package util

import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.ffmpeg.VideoWriter
import timestamp

const val widthUHD = 3840
const val heightUHD = 2160

private lateinit var rt: RenderTarget
private lateinit var resolved: ColorBuffer

fun Drawer.multisampling(sampleCount: Int, function: Drawer.() -> Unit) {
    if (!::rt.isInitialized) {
        rt = renderTarget(width, height, multisample = BufferMultisample.SampleCount(sampleCount)) {
            colorBuffer()
            depthBuffer()
        }
    } else {
        rt.clearColor(0, ColorRGBa.BLACK)
        rt.clearDepth()
    }
    isolatedWithTarget(rt) {
        function()
    }
    if (!::resolved.isInitialized) {
        resolved = colorBuffer(width, height)
    }
    rt.colorBuffer(0).copyTo(resolved)
    image(resolved)
}

fun Program.videoExtend(
    outputName: String = "video-${timestamp()}.mp4",
    onFrame: Drawer.(frame: Int, seconds: Double) -> Boolean
) {
    val videoWriter = VideoWriter.create().size(width, height).output("$outputName.mp4").start()
    val videoTarget = renderTarget(width, height) {
        colorBuffer()
        depthBuffer()
    }
    drawer.isolatedWithTarget(videoTarget) {
        var frame = 0
        while (onFrame(frame, frame / 60.0)) {
            videoWriter.frame(videoTarget.colorBuffer(0))
            println("processed frame ${frame + 1}")
            frame++
        }
        println("program end")
    }
    videoWriter.stop()
    application.exit()
}

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
