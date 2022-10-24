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
    var pixelsInUnit: Double,
) {
    constructor(p: Program, xc: Double, yc: Double, pixelsInUnit: Double) :
            this(p.width, p.height, xc, yc, pixelsInUnit)

    val width = width.toDouble()
    val height = height.toDouble()

    fun fromReal(x: Number, y: Number): Pair<Double, Double> {
        return Pair(
            ((x.toDouble() - xc) * pixelsInUnit + width / 2.0),
            (-(y.toDouble() - yc) * pixelsInUnit + height / 2.0)
        )
    }

    fun fromReal(real: Vector2): Vector2 {
        return Vector2(
            ((real.x - xc) * pixelsInUnit + width / 2.0),
            (-(real.y - yc) * pixelsInUnit + height / 2.0)
        )
    }

    fun fromPx(j: Number, i: Number): Pair<Double, Double> {
        return Pair(
            (j.toDouble() - width / 2.0) / pixelsInUnit + xc,
            -(i.toDouble() - height / 2.0) / pixelsInUnit + yc
        )
    }

    fun fromPx(px: Vector2): Vector2 {
        return Vector2(
            (px.x - width / 2.0) / pixelsInUnit + xc,
            -(px.y - height / 2.0) / pixelsInUnit + yc
        )
    }

    interface RealScope {
        val t: Vector2
    }

    fun realScope(px: Vector2, realBlock: RealScope.() -> Vector2): Vector2 {
        return fromReal(object : RealScope {
            override val t get() = fromPx(px)
        }.realBlock())
    }

    fun <T> withReal(px: Vector2, realBlock: (Vector2) -> T) = realBlock(fromPx(px))
}

fun Pair<Double, Double>.toVector2() = Vector2(first, second)
fun Vector2.toPair() = Pair(x, y)
