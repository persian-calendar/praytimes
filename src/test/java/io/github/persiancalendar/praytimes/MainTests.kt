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
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )

        assertEquals(Clock(5, 9).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 48).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 21).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.ISNA,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 27).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 48).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 9).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Egypt,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 0).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 48).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 24).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Makkah,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 6).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 48).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 18).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Karachi,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 9).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 48).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 27).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Jafari,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 21).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(20, 5).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 3).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Tehran,
            getDate("GMT-4:00", 2018, 9, 5),
            Coordinate(43.0, -80.0, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 11).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(6, 49).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 19).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 57).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(20, 8).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(21, 3).toInt(), prayTimes.isha.toInt())

        prayTimes = PrayTimes(
            CalculationMethod.Tehran,
            getDate("GMT+8:00", 2019, 6, 9),
            Coordinate(3.147778, 101.695278, 0.0),
            CalculationMethod.AsrJuristics.Standard
        )
        assertEquals(Clock(5, 49).toInt(), prayTimes.fajr.toInt())
        assertEquals(Clock(7, 3).toInt(), prayTimes.sunrise.toInt())
        assertEquals(Clock(13, 12).toInt(), prayTimes.dhuhr.toInt())
        assertEquals(Clock(16, 39).toInt(), prayTimes.asr.toInt())
        assertEquals(Clock(19, 37).toInt(), prayTimes.maghrib.toInt())
        assertEquals(Clock(20, 19).toInt(), prayTimes.isha.toInt())
    }

    private fun getDate(timeZone: String, year: Int, month: Int, dayOfMonth: Int): GregorianCalendar =
        GregorianCalendar(TimeZone.getTimeZone(timeZone)).apply {
            set(year, month - 1, dayOfMonth, 0, 0)
        }
}
