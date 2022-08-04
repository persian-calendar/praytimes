@file:JvmName("Calculate")

package io.github.persiancalendar.praytimes

import java.util.*

@JvmOverloads
fun prayTimes(
    method: CalculationMethod,
    calendar: GregorianCalendar,
    coordinates: Coordinates,
    asrMethod: AsrMethod = AsrMethod.Standard,
    highLatitudesMethod: HighLatitudesMethod = HighLatitudesMethod.NightMiddle
): PrayTimes {
    val year = calendar[GregorianCalendar.YEAR]
    val month = calendar[GregorianCalendar.MONTH] + 1
    val day = calendar[GregorianCalendar.DAY_OF_MONTH]
    val offset = calendar.timeZone.getOffset(calendar.time.time) / (60 * 60 * 1000.0)
    return PrayTimes(method, year, month, day, offset, coordinates, asrMethod, highLatitudesMethod)
}
