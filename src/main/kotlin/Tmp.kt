import util.diff
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

const val eps = 1e-6

fun ((Double) -> Double).integrate(from: Double = 0.0, to: Double = 1.0): Double {
    return generateSequence(from) { (it + eps).takeIf { it < to } }.sumOf { this(it) * eps }
}

fun ((Double) -> Double).curveLength(from: Double = 0.0, to: Double = 1.0): Double {
    val df = this.diff
    val f = { x: Double -> sqrt(1.0 + df(x).pow(2)) }
    return f.integrate()
}

// f > g,   f'' < 0, g'' < 0,   f(0)=g(0), f(1)=g(1)
val f = { x: Double -> (0.5.pow(4) - (x - 0.5).pow(4)) / 0.5.pow(4) }
val g = { x: Double -> sin(x * PI) }

fun main() {
//    println(f.curveLength())
//    println(g.curveLength())
    for (n in 1..5) {
        println("n=$n")
        println("int f^2n = ${{ x: Double -> f(x).pow(2 * n) }.integrate()}")
        println("int g^2n = ${{ x: Double -> g(x).pow(2 * n) }.integrate()}")
    }
}
