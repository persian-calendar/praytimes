package io.github.persiancalendar.praytimes

import io.github.persiancalendar.praytimes.CalculationMethod.MinuteOrAngleDouble
import io.github.persiancalendar.praytimes.CalculationMethod.MinuteOrAngleDouble.Companion.deg
import io.github.persiancalendar.praytimes.CalculationMethod.MinuteOrAngleDouble.Companion.min
import java.util.*
import kotlin.math.*

class PrayTimes @JvmOverloads constructor(
    method: CalculationMethod,
    calendar: GregorianCalendar,
    coordinates: Coordinates,
    asrMethod: AsrMethod = AsrMethod.Standard,
    highLatitudesMethod: HighLatitudesMethod = HighLatitudesMethod.NightMiddle
) {
    // A real number [0-24) of a day, up to client to turn it to hours and minutes, and either show seconds or round it
    val imsak: Double
    val fajr: Double
    val sunrise: Double
    val dhuhr: Double
    val asr: Double
    val sunset: Double
    val maghrib: Double
    val isha: Double
    val midnight: Double

    init {
        val year = calendar[GregorianCalendar.YEAR]
        val month = calendar[GregorianCalendar.MONTH] + 1
        val day = calendar[GregorianCalendar.DAY_OF_MONTH]
        val jdate = julian(year, month, day) - coordinates.longitude / (15.0 * 24.0)

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
        run {
            val offset = calendar.timeZone.getOffset(calendar.time.time) / (60 * 60 * 1000.0)
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
                    highLatitudesMethod,
                    imsak,
                    sunrise,
                    DEFAULT_TIME_IMSAK.value,
                    nightTime,
                    true
                )
                fajr = adjustHLTime(
                    highLatitudesMethod,
                    fajr,
                    sunrise,
                    method.fajr.value,
                    nightTime,
                    true
                )
                isha = adjustHLTime(highLatitudesMethod, isha, sunset, method.isha.value, nightTime)
                maghrib = adjustHLTime(
                    highLatitudesMethod,
                    maghrib,
                    sunset,
                    method.maghrib.value,
                    nightTime
                )
            }
            if (DEFAULT_TIME_IMSAK.isMinutes) {
                imsak = fajr - DEFAULT_TIME_IMSAK.value / 60.0
            }
            if (method.maghrib.isMinutes) {
                maghrib = sunset + method.maghrib.value / 60.0
            }
            if (method.isha.isMinutes) {
                isha = maghrib + method.isha.value / 60.0
            }
            dhuhr += DEFAULT_TIME_DHUHR.value / 60.0
        }

        // add midnight time
        val midnight =
            if (method.midnight === CalculationMethod.MidnightType.Jafari) sunset + timeDiff(
                sunset,
                fajr
            ) / 2 else sunset + timeDiff(sunset, sunrise) / 2
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
    companion object {
        // default times
        private const val DEFAULT_IMSAK = 5.0 / 24
        private const val DEFAULT_FAJR = 5.0 / 24
        private const val DEFAULT_SUNRISE = 6.0 / 24
        private const val DEFAULT_DHUHR = 12.0 / 24
        private const val DEFAULT_ASR = 13.0 / 24
        private const val DEFAULT_SUNSET = 18.0 / 24
        private const val DEFAULT_MAGHRIB = 18.0 / 24
        private const val DEFAULT_ISHA = 18.0 / 24
        private val DEFAULT_TIME_IMSAK = min(10)
        private val DEFAULT_TIME_DHUHR = min(0)

        // compute mid-day time
        private fun midDay(jdate: Double, time: Double): Double {
            val eqt = sunPosition(jdate + time).equation
            return fixHour(12 - eqt)
        }

        // compute the time at which sun reaches a specific angle below horizon
        private fun sunAngleTime(
            jdate: Double,
            angle: MinuteOrAngleDouble,
            time: Double,
            ccw: Boolean,
            coordinates: Coordinates
        ): Double {
            // TODO: the below assert should be considered
            // if (angle.isMinute()) throw new IllegalArgumentException("angle argument must be degree, not minute!");
            val decl = sunPosition(jdate + time).declination
            val noon = Math.toRadians(midDay(jdate, time))
            val t = acos(
                (-sin(Math.toRadians(angle.value)) - sin(decl)
                        * sin(Math.toRadians(coordinates.latitude)))
                        / (cos(decl) * cos(Math.toRadians(coordinates.latitude)))
            ) / 15.0
            return Math.toDegrees(noon + if (ccw) -t else t)
        }

        private fun sunAngleTime(
            jdate: Double,
            angle: MinuteOrAngleDouble,
            time: Double,
            coordinates: Coordinates
        ): Double {
            return sunAngleTime(jdate, angle, time, false, coordinates)
        }

        // compute asr time
        private fun asrTime(
            jdate: Double,
            factor: Double,
            time: Double,
            coordinates: Coordinates
        ): Double {
            val decl = sunPosition(jdate + time).declination
            val angle =
                -atan(1 / (factor + tan(abs(Math.toRadians(coordinates.latitude) - decl))))
            return sunAngleTime(jdate, deg(Math.toDegrees(angle)), time, coordinates)
        }

        // compute declination angle of sun and equation of time
        // Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
        private fun sunPosition(jd: Double): DeclEqt {
            val D = jd - 2451545.0
            val g = (357.529 + .98560028 * D) % 360
            val q = (280.459 + .98564736 * D) % 360
            val L =
                (q + 1.915 * Math.sin(Math.toRadians(g)) + .020 * Math.sin(Math.toRadians(2.0 * g))) % 360

            // weird!
            // double R = 1.00014 - 0.01671 * Math.cos(dtr(g)) - 0.00014 *
            // Math.cos(dtr(2d * g));
            val e = 23.439 - .00000036 * D
            val RA = Math.toDegrees(
                atan2(
                    cos(Math.toRadians(e)) * sin(Math.toRadians(L)),
                    cos(Math.toRadians(L))
                )
            ) / 15.0
            val eqt = q / 15.0 - fixHour(RA)
            val decl = asin(sin(Math.toRadians(e)) * sin(Math.toRadians(L)))
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
            return deg(.833 + angle)
        }

        // adjust a time for higher latitudes
        private fun adjustHLTime(
            highLatMethod: HighLatitudesMethod,
            time: Double, bbase: Double, angle: Double,
            night: Double, ccw: Boolean = false
        ): Double {
            var time = time
            val portion = nightPortion(highLatMethod, angle, night)
            val timeDiff = if (ccw) timeDiff(time, bbase) else timeDiff(bbase, time)
            if (java.lang.Double.isNaN(time) || timeDiff > portion) time =
                bbase + if (ccw) -portion else portion
            return time
        }

        // the night portion used for adjusting times in higher latitudes
        private fun nightPortion(
            highLatMethod: HighLatitudesMethod,
            angle: Double,
            night: Double
        ): Double {
            var portion = 1.0 / 2.0
            if (highLatMethod === HighLatitudesMethod.AngleBased) {
                portion = 1.0 / 60.0 * angle
            }
            if (highLatMethod === HighLatitudesMethod.OneSeventh) {
                portion = 1.0 / 7.0
            }
            return portion * night
        }

        // compute the difference between two times
        private fun timeDiff(time1: Double, time2: Double): Double {
            return fixHour(time2 - time1)
        }

        fun fixHour(a: Double): Double {
            val result = a % 24
            return if (result < 0) 24 + result else result
        }
    }
}