package util

import org.openrndr.Program
import org.openrndr.math.Vector2

// (x, y) --> real
// (j, i) --> px
class RealPx(
    width: Number,
    height: Number,
    var xc: Double,
    var yc: Double,
    var scale: Double, // how many pixels in one real unit
) {
    constructor(p: Program, xc: Double, yc: Double, scale: Double) : this(p.width, p.height, xc, yc, scale)

    val width = width.toDouble()
    val height = height.toDouble()

    fun realToPx(x: Number, y: Number): Pair<Double, Double> {
        return Pair(
            ((x.toDouble() - xc) * scale + width.toDouble() / 2.0),
            (-(y.toDouble() - yc) * scale + height.toDouble() / 2.0)
        )
    }

    fun realToPx(real: Vector2): Vector2 {
        return Vector2(
            ((real.x - xc) * scale + width.toDouble() / 2.0),
            (-(real.y - yc) * scale + height.toDouble() / 2.0)
        )
    }

    fun pxToReal(j: Number, i: Number): Pair<Double, Double> {
        return Pair(
            (j.toDouble() - width.toDouble() / 2.0) / scale + xc,
            -(i.toDouble() - height.toDouble() / 2.0) / scale + yc
        )
    }

    fun pxToReal(px: Vector2): Vector2 {
        return Vector2(
            (px.x - width.toDouble() / 2.0) / scale + xc,
            -(px.y - height.toDouble() / 2.0) / scale + yc
        )
    }

    interface RealScope {
        val t: Vector2
    }

    fun realScope(px: Vector2, realBlock: RealScope.() -> Vector2): Vector2 {
        return realToPx(object : RealScope {
            override val t get() = pxToReal(px)
        }.realBlock())
    }
}

fun Pair<Double, Double>.toVector2() = Vector2(first, second)
fun Vector2.toPair() = Pair(x, y)
