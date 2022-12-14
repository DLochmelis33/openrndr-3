package util

import org.openrndr.math.Vector2

private const val h = 1e-6

class VectorField private constructor(private val f: (Vector2) -> Vector2) : ((Vector2) -> Vector2) by f {

    val divergence: ScalarField by lazy {
        ScalarField { v ->
            val xh = Vector2.UNIT_X * h
            val yh = Vector2.UNIT_Y * h
            val dxv = (this(v + xh) - this(v - xh)) / (2 * h)
            val dyv = (this(v + yh) - this(v - yh)) / (2 * h)
            dxv.x + dyv.y
        }
    }

    val curl2d: ScalarField by lazy {
        ScalarField { v ->
            val xh = Vector2.UNIT_X * h
            val yh = Vector2.UNIT_Y * h
            val dxv = (this(v + xh) - this(v - xh)) / (2 * h)
            val dyv = (this(v + yh) - this(v - yh)) / (2 * h)
            dxv.x - dyv.y
        }
    }

    companion object {
        operator fun invoke(f: (Vector2) -> Vector2) = VectorField(f)
    }
}

class ScalarField private constructor(private val f: (Vector2) -> Double) : ((Vector2) -> Double) by f {

    val gradient: VectorField by lazy {
        VectorField { v ->
            val xh = Vector2(h, 0.0)
            val yh = Vector2(0.0, h)
            val dx = (this(v + xh) - this(v - xh)) / (2 * h)
            val dy = (this(v + yh) - this(v - yh)) / (2 * h)
            Vector2(dx, dy)
        }
    }

    companion object {
        operator fun invoke(f: (Vector2) -> Double) = ScalarField(f)
    }
}

val ((Double) -> Double).diff
    get() = { x: Double ->
        (this(x + h) - this(x - h)) / (2 * h)
    }
