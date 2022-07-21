package io.github.persiancalendar.praytimes

import kotlin.jvm.JvmOverloads
import io.github.persiancalendar.praytimes.CalculationMethod.MinuteOrAngleDouble
import io.github.persiancalendar.praytimes.CalculationMethod
import io.github.persiancalendar.praytimes.CalculationMethod.MidnightType

enum class CalculationMethod @JvmOverloads constructor(
    val fajr: MinuteOrAngleDouble,
    val isha: MinuteOrAngleDouble,
    val maghrib: MinuteOrAngleDouble = min(0),
    val midnight: MidnightType = MidnightType.Standard
) {
    // Muslim World League
    MWL(deg(18), deg(17)),  // Islamic Society of North America (ISNA)
    ISNA(deg(15), deg(15)),  // Egyptian General Authority of Survey
    Egypt(deg(19.5), deg(17.5)),  // Umm Al-Qura University, Makkah
    Makkah(deg(18.5), min(90)),  // University of Islamic Sciences, Karachi
    Karachi(deg(18), deg(18)),  // Institute of Geophysics, University of Tehran
    Tehran(
        deg(17.7),
        deg(14),
        deg(4.5),
        MidnightType.Jafari
    ),  // Shia Ithna-Ashari, Leva Institute, Qum
    Jafari(deg(16), deg(14), deg(4), MidnightType.Jafari);

    val isJafari: Boolean

    init {
        isJafari = midnight == MidnightType.Jafari
    }

    // Midnight Mode
    enum class MidnightType {
        Standard,  // Mid Sunset to Sunrise
        Jafari // Mid Sunset to Fajr
    }

    class MinuteOrAngleDouble(val value: Double, val isMinutes: Boolean)
    companion object {
        fun deg(value: Int): MinuteOrAngleDouble {
            return deg(value.toDouble())
        }

        @JvmStatic
        fun min(value: Int): MinuteOrAngleDouble {
            return MinuteOrAngleDouble(value.toDouble(), true)
        }

        @JvmStatic
        fun deg(value: Double): MinuteOrAngleDouble {
            return MinuteOrAngleDouble(value, false)
        }
    }
}