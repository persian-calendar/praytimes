package io.github.persiancalendar.praytimes

enum class CalculationMethod(
    internal val fajr: MinuteOrAngleDouble,
    internal val isha: MinuteOrAngleDouble,
    internal val maghrib: MinuteOrAngleDouble = 0.min,
    internal val midnight: MidnightMethod = MidnightMethod.MidSunsetToSunrise
) {
    /** Muslim World League */
    MWL(18.deg, 17.deg),

    /** Islamic Society of North America (ISNA) */
    ISNA(15.deg, 15.deg),

    /** Egyptian General Authority of Survey */
    Egypt(19.5.deg, 17.5.deg),

    /** Umm Al-Qura University, Makkah */
    Makkah(18.5.deg, 90.min),

    /** University of Islamic Sciences, Karachi */
    Karachi(18.deg, 18.deg),

    /** Institute of Geophysics, University of Tehran */
    Tehran(17.7.deg, 14.deg, 4.5.deg, MidnightMethod.MidSunsetToFajr),

    /** Shia Ithna-Ashari, Leva Institute, Qum */
    Jafari(16.deg, 14.deg, 4.deg, MidnightMethod.MidSunsetToFajr);

    /** Is the calculation method a Jafari one */
    val isJafari = midnight == MidnightMethod.MidSunsetToFajr // Jafari's default is SunsetToFajr

}
