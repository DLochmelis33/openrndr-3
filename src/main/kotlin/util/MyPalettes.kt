package util

import nextDouble
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import kotlin.random.Random

fun ColorHSVa.complementary() = shiftHue(180.0)
fun ColorRGBa.complementary() = toHSVa().complementary().toRGBa()

fun ColorHSVa.analogous() = listOf(-30.0, +30.0).map { shiftHue(it) }
fun ColorRGBa.analogous() = toHSVa().analogous().map { it.toRGBa() }

fun ColorRGBa.shiftHue(degrees: Double) = toHSVa().shiftHue(degrees).toRGBa()

fun randomPastel(r: Random = Random): ColorHSVa {
    val rangeH = 0.0..360.0
    val rangeS = 0.1..0.5
    val rangeV = 0.2..1.0
    return ColorHSVa(r.nextDouble(rangeH), r.nextDouble(rangeS), r.nextDouble(rangeV), 1.0)
}
