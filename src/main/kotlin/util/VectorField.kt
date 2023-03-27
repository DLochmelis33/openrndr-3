package util

import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

private const val h = 1e-6

typealias VF = (Vector2) -> Vector2
typealias SF = (Vector2) -> Double

val VF.div: SF
    get() = { v ->
        val xh = Vector2.UNIT_X * h
        val yh = Vector2.UNIT_Y * h
        val dxv = (this(v + xh) - this(v - xh)) / (2 * h)
        val dyv = (this(v + yh) - this(v - yh)) / (2 * h)
        dxv.x + dyv.y
    }

val VF.curl: SF
    get() = { v ->
        val xh = Vector2.UNIT_X * h
        val yh = Vector2.UNIT_Y * h
        val dxv = (this(v + xh) - this(v - xh)) / (2 * h)
        val dyv = (this(v + yh) - this(v - yh)) / (2 * h)
        dxv.x - dyv.y
    }

val SF.grad: VF
    get() = { v ->
        val xh = Vector2(h, 0.0)
        val yh = Vector2(0.0, h)
        val dx = (this(v + xh) - this(v - xh)) / (2 * h)
        val dy = (this(v + yh) - this(v - yh)) / (2 * h)
        Vector2(dx, dy)
    }

fun SF.normalAt(at: Vector2): Vector3 {
    val (dx, dy) = grad(at)
    return Vector3(-dx, -dy, 1.0)
}

val ((Double) -> Double).diff
    get() = { x: Double ->
        (this(x + h) - this(x - h)) / (2 * h)
    }

typealias SF3 = (Vector3) -> Double
typealias VF3 = (Vector3) -> Vector3

val SF3.grad: VF3
    @JvmName("sf3grad")
    get() = { v ->
        val xh = Vector3.UNIT_X * h
        val yh = Vector3.UNIT_Y * h
        val zh = Vector3.UNIT_Z * h
        val dx = (this(v + xh) - this(v - xh)) / (2 * h)
        val dy = (this(v + yh) - this(v - yh)) / (2 * h)
        val dz = (this(v + zh) - this(v - zh)) / (2 * h)
        Vector3(dx, dy, dz)
    }
