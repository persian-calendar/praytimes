package io.github.persiancalendar.praytimes

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class MainTests {

    @Test
    fun `pray times calculations correctness`() {
        // http://praytimes.org/code/v2/js/examples/monthly.htm
        var prayTimes = PrayTimes(
            CalculationMethod.MWL,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )

        assertEquals(Clock(5, 9).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 48).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 21).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.ISNA,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 27).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 48).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 9).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Egypt,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Hanafi
        )
        assertEquals(Clock(5, 0).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(17, 53).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 48).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 24).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Makkah,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 6).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 48).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 18).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Karachi,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 9).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 48).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 27).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Jafari,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 21).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(20, 5).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 3).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Tehran,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinates(43.0, -80.0, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 11).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(6, 49).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 19).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 57).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(20, 8).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(21, 3).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())

        prayTimes = PrayTimes(
            CalculationMethod.Tehran,
            getDate("GMT+8:00", 2019, 6, 9),
            Coordinates(3.147778, 101.695278, 0.0),
            AsrMethod.Standard
        )
        assertEquals(Clock(5, 49).toMinutes(), prayTimes.fajr.toRoundedClock().toMinutes())
        assertEquals(Clock(7, 3).toMinutes(), prayTimes.sunrise.toRoundedClock().toMinutes())
        assertEquals(Clock(13, 12).toMinutes(), prayTimes.dhuhr.toRoundedClock().toMinutes())
        assertEquals(Clock(16, 39).toMinutes(), prayTimes.asr.toRoundedClock().toMinutes())
        assertEquals(Clock(19, 37).toMinutes(), prayTimes.maghrib.toRoundedClock().toMinutes())
        assertEquals(Clock(20, 19).toMinutes(), prayTimes.isha.toRoundedClock().toMinutes())
    }

    private data class Clock(val hours: Int, val minutes: Int) {
        fun toMinutes() = hours * 60 + minutes
    }

    private fun Double.toRoundedClock(): Clock {
        val rounded = (this + 0.5 / 60) % 24 // add 0.5 minutes to round
        val value = if (rounded < 0) (24 + rounded) else rounded
        val hours = value.toInt()
        val minutes = ((value - hours) * 60.0).toInt()
        return Clock(hours, minutes)
    }

    private fun getDate(timeZone: String, year: Int, month: Int, dayOfMonth: Int): GregorianCalendar =
        GregorianCalendar(TimeZone.getTimeZone(timeZone)).apply {
            set(year, month - 1, dayOfMonth, 0, 0)
        }
}