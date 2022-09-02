package io.github.persiancalendar.praytimes

// Adjust Methods for Higher Latitudes
enum class HighLatitudesMethod {
    /** middle of night, the default */
    NightMiddle,

    /** angle/60th of night */
    AngleBased,

    /** 1/7th of night */
    OneSeventh,

    /** No adjustment */
    None
}
