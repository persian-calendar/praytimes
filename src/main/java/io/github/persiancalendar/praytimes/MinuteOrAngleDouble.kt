package io.github.persiancalendar.praytimes

data class MinuteOrAngleDouble(@JvmField val value: Double, @JvmField val isMinutes: Boolean)

fun deg(value: Number): MinuteOrAngleDouble {
    return MinuteOrAngleDouble(value.toDouble(), false)
}

fun min(value: Int): MinuteOrAngleDouble {
    return MinuteOrAngleDouble(value.toDouble(), true)
}
