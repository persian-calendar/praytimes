package io.github.persiancalendar.praytimes

import kotlin.math.*

class PrayTimes(
    /** Variant of pray times calculation */
    method: CalculationMethod,
    /** Gregorian calendar year, e.g. 2020 */
    year: Int,
    /** Gregorian calendar month of year, 1-12 (NOT zero-indexed) */
    month: Int,
    /** Gregorian calendar day of month, 1-31 */
    dayOfMonth: Int,
    /** Time zone offset in milliseconds, for GMT-1:00 it is -1, for GMT+3:30 it is 3.5 */
    offset: Double,
    /** Coordinate on earth */
    coordinates: Coordinates,
    /** Asr calculation method, can be either AsrMethod.Standard or AsrMethod.Hanafi */
    asrMethod: AsrMethod,
    /** Correcting used for high latitudes places. Default is HighLatitudesMethod.NightMiddle */
    highLatitudesMethod: HighLatitudesMethod
) {
    // A real number [0-24) of a day, up to client to turn it to hours and minutes,
    // and either show seconds or round it
    val imsak: Double
    val fajr: Double
    val sunrise: Double
    val dhuhr: Double
    val asr: Double
    val sunset: Double
    val maghrib: Double
    val isha: Double
    val midnight: Double

    // default times
    companion object {
        private const val DEFAULT_IMSAK = 5.0 / 24
        private const val DEFAULT_FAJR = 5.0 / 24
        private const val DEFAULT_SUNRISE = 6.0 / 24
        private const val DEFAULT_DHUHR = 12.0 / 24
        private const val DEFAULT_ASR = 13.0 / 24
        private const val DEFAULT_SUNSET = 18.0 / 24
        private const val DEFAULT_MAGHRIB = 18.0 / 24
        private const val DEFAULT_ISHA = 18.0 / 24
        private val DEFAULT_TIME_IMSAK = 10.min
        private val DEFAULT_TIME_DHUHR = 0.min
    }

    init {
        val jdate = julian(year, month, dayOfMonth) - coordinates.longitude / (15.0 * 24.0)
        // compute prayer times at given julian date
        var imsak = sunAngleTime(jdate, DEFAULT_TIME_IMSAK, DEFAULT_IMSAK, true, coordinates)
        var fajr = sunAngleTime(jdate, method.fajr, DEFAULT_FAJR, true, coordinates)
        var sunrise =
            sunAngleTime(jdate, riseSetAngle(coordinates), DEFAULT_SUNRISE, true, coordinates)
        var dhuhr = midDay(jdate, DEFAULT_DHUHR)
        var asr = asrTime(jdate, asrMethod.asrFactor, DEFAULT_ASR, coordinates)
        var sunset = sunAngleTime(jdate, riseSetAngle(coordinates), DEFAULT_SUNSET, coordinates)
        var maghrib = sunAngleTime(jdate, method.maghrib, DEFAULT_MAGHRIB, coordinates)
        var isha = sunAngleTime(jdate, method.isha, DEFAULT_ISHA, coordinates)

        // Adjust times
        val addToAll = offset - coordinates.longitude / 15.0
        imsak += addToAll
        fajr += addToAll
        sunrise += addToAll
        dhuhr += addToAll
        asr += addToAll
        sunset += addToAll
        maghrib += addToAll
        isha += addToAll
        if (highLatitudesMethod !== HighLatitudesMethod.None) {
            // adjust times for locations in higher latitudes
            val nightTime = timeDiff(sunset, sunrise)
            imsak = adjustHLTime(
                highLatitudesMethod, imsak, sunrise, DEFAULT_TIME_IMSAK.value, nightTime, true
            )
            fajr = adjustHLTime(
                highLatitudesMethod, fajr, sunrise, method.fajr.value, nightTime, true
            )
            isha = adjustHLTime(highLatitudesMethod, isha, sunset, method.isha.value, nightTime)
            maghrib = adjustHLTime(
                highLatitudesMethod, maghrib, sunset, method.maghrib.value, nightTime
            )
        }
        if (DEFAULT_TIME_IMSAK.isMinutes) imsak = fajr - DEFAULT_TIME_IMSAK.value / 60.0
        if (method.maghrib.isMinutes) maghrib = sunset + method.maghrib.value / 60.0
        if (method.isha.isMinutes) isha = maghrib + method.isha.value / 60.0
        dhuhr += DEFAULT_TIME_DHUHR.value / 60.0

        // add midnight time
        val midnight = if (method.midnight === CalculationMethod.MidnightType.Jafari)
            sunset + timeDiff(sunset, fajr) / 2 else sunset + timeDiff(sunset, sunrise) / 2
        this.imsak = fixHour(imsak)
        this.fajr = fixHour(fajr)
        this.sunrise = fixHour(sunrise)
        this.dhuhr = fixHour(dhuhr)
        this.asr = fixHour(asr)
        this.sunset = fixHour(sunset)
        this.maghrib = fixHour(maghrib)
        this.isha = fixHour(isha)
        this.midnight = fixHour(midnight)
    }

    private class DeclEqt(val declination: Double, val equation: Double)

    // compute mid-day time
    private fun midDay(jdate: Double, time: Double): Double =
        fixHour(12 - sunPosition(jdate + time).equation)

    // compute the time at which sun reaches a specific angle below horizon
    private fun sunAngleTime(
        jdate: Double, angle: MinuteOrAngleDouble, time: Double, ccw: Boolean,
        coordinates: Coordinates
    ): Double {
        // TODO: the below assert should be considered
        // if (angle.isMinute()) throw new IllegalArgumentException("angle argument must be degree, not minute!");
        val decl = sunPosition(jdate + time).declination
        val noon = midDay(jdate, time).toRadians
        val t = acos(
            (-sin(angle.value.toRadians) - sin(decl)
                    * sin(coordinates.latitude.toRadians))
                    / (cos(decl) * cos(coordinates.latitude.toRadians))
        ) / 15.0
        return (noon + if (ccw) -t else t).toDegrees
    }

    private fun sunAngleTime(
        jdate: Double, angle: MinuteOrAngleDouble, time: Double, coordinates: Coordinates
    ): Double {
        return sunAngleTime(jdate, angle, time, false, coordinates)
    }

    // compute asr time
    private fun asrTime(
        jdate: Double, factor: Double, time: Double, coordinates: Coordinates
    ): Double {
        val decl = sunPosition(jdate + time).declination
        val angle = -atan(1 / (factor + tan(abs(coordinates.latitude.toRadians - decl))))
        return sunAngleTime(jdate, angle.toDegrees.deg, time, coordinates)
    }

    // compute declination angle of sun and equation of time
    // Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
    private fun sunPosition(jd: Double): DeclEqt {
        val D = jd - 2451545.0
        val g = (357.529 + .98560028 * D) % 360
        val q = (280.459 + .98564736 * D) % 360
        val L = (q + 1.915 * sin(g.toRadians) + .020 * sin((2.0 * g).toRadians)) % 360

        // weird!
        // double R = 1.00014 - 0.01671 * Math.cos(dtr(g)) - 0.00014 *
        // Math.cos(dtr(2d * g));
        val e = 23.439 - .00000036 * D
        val RA = atan2(cos(e.toRadians) * sin(L.toRadians), cos(L.toRadians)).toDegrees / 15
        val eqt = q / 15.0 - fixHour(RA)
        val decl = asin(sin(e.toRadians) * sin(L.toRadians))
        return DeclEqt(decl, eqt)
    }

    // convert Gregorian date to Julian day
    // Ref: Astronomical Algorithms by Jean Meeus
    private fun julian(year: Int, month: Int, day: Int): Double {
        var year = year
        var month = month
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val A = floor(year.toDouble() / 100)
        val B = 2 - A + floor(A / 4)
        return floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1)) + day + B - 1524.5
    }

    // Section 2!! (Compute Prayer Time in JS code)
    //
    // return sun angle for sunset/sunrise
    private fun riseSetAngle(coordinates: Coordinates): MinuteOrAngleDouble {
        // var earthRad = 6371009; // in meters
        // var angle = DMath.arccos(earthRad/(earthRad+ elv));
        val angle = .0347 * sqrt(coordinates.elevation.coerceAtLeast(0.0)) // an approximation
        return (.833 + angle).deg
    }

    // adjust a time for higher latitudes
    private fun adjustHLTime(
        highLatMethod: HighLatitudesMethod, time: Double, bbase: Double, angle: Double,
        night: Double, ccw: Boolean = false
    ): Double {
        var time = time
        val portion = nightPortion(highLatMethod, angle, night)
        val timeDiff = if (ccw) timeDiff(time, bbase) else timeDiff(bbase, time)
        if (time.isNaN() || timeDiff > portion)
            time = bbase + if (ccw) -portion else portion
        return time
    }

    // the night portion used for adjusting times in higher latitudes
    private fun nightPortion(
        highLatMethod: HighLatitudesMethod, angle: Double, night: Double
    ): Double {
        var portion = .5
        if (highLatMethod === HighLatitudesMethod.AngleBased) portion = 1.0 / 60.0 * angle
        if (highLatMethod === HighLatitudesMethod.OneSeventh) portion = 1.0 / 7.0
        return portion * night
    }

    // compute the difference between two times
    private fun timeDiff(time1: Double, time2: Double) = fixHour(time2 - time1)

    private fun fixHour(a: Double): Double {
        val result = a % 24
        return if (result < 0) 24 + result else result
    }

    private val Double.toRadians get() = this * PI / 180
    private val Double.toDegrees get() = this * 180 / PI
}
