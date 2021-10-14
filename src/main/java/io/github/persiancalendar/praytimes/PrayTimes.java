package io.github.persiancalendar.praytimes;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static io.github.persiancalendar.praytimes.CalculationMethod.deg;
import static io.github.persiancalendar.praytimes.CalculationMethod.min;

public class PrayTimes {

    // A real number [0-24) of a day, up to client to turn it to hours and minutes, and either show seconds or round it
    public final double imsak, fajr, sunrise, dhuhr, asr, sunset, maghrib, isha, midnight;

    public PrayTimes(CalculationMethod method, GregorianCalendar calendar, Coordinates coordinates,
                     AsrMethod asrMethod) {
        this(method, calendar, coordinates, asrMethod, HighLatitudesMethod.NightMiddle);
    }

    public PrayTimes(CalculationMethod method, GregorianCalendar calendar, Coordinates coordinates,
                     AsrMethod asrMethod,
                     HighLatitudesMethod highLatitudesMethod) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        double jdate = julian(year, month, day) - coordinates.longitude / (15. * 24.);

        // compute prayer times at given julian date
        double imsak = sunAngleTime(jdate, DEFAULT_TIME_IMSAK, DEFAULT_IMSAK, true, coordinates);
        double fajr = sunAngleTime(jdate, method.fajr, DEFAULT_FAJR, true, coordinates);
        double sunrise = sunAngleTime(jdate, riseSetAngle(coordinates), DEFAULT_SUNRISE, true, coordinates);
        double dhuhr = midDay(jdate, DEFAULT_DHUHR);
        double asr = asrTime(jdate, asrMethod.asrFactor, DEFAULT_ASR, coordinates);
        double sunset = sunAngleTime(jdate, riseSetAngle(coordinates), DEFAULT_SUNSET, coordinates);
        double maghrib = sunAngleTime(jdate, method.maghrib, DEFAULT_MAGHRIB, coordinates);
        double isha = sunAngleTime(jdate, method.isha, DEFAULT_ISHA, coordinates);

        // Adjust times
        {
            double offset = calendar.getTimeZone().getOffset(calendar.getTime().getTime()) / (60 * 60 * 1000.0);
            double addToAll = offset - coordinates.longitude / 15.;
            imsak += addToAll;
            fajr += addToAll;
            sunrise += addToAll;
            dhuhr += addToAll;
            asr += addToAll;
            sunset += addToAll;
            maghrib += addToAll;
            isha += addToAll;

            if (highLatitudesMethod != HighLatitudesMethod.None) {
                // adjust times for locations in higher latitudes
                double nightTime = timeDiff(sunset, sunrise);

                imsak = adjustHLTime(highLatitudesMethod, imsak, sunrise, DEFAULT_TIME_IMSAK.value, nightTime, true);
                fajr = adjustHLTime(highLatitudesMethod, fajr, sunrise, method.fajr.value, nightTime, true);
                isha = adjustHLTime(highLatitudesMethod, isha, sunset, method.isha.value, nightTime);
                maghrib = adjustHLTime(highLatitudesMethod, maghrib, sunset, method.maghrib.value, nightTime);
            }

            if (DEFAULT_TIME_IMSAK.isMinutes) {
                imsak = fajr - DEFAULT_TIME_IMSAK.value / 60.;
            }
            if (method.maghrib.isMinutes) {
                maghrib = sunset + method.maghrib.value / 60.;
            }
            if (method.isha.isMinutes) {
                isha = maghrib + method.isha.value / 60.;
            }
            dhuhr = dhuhr + DEFAULT_TIME_DHUHR.value / 60.;
        }

        // add midnight time
        double midnight = method.midnight == CalculationMethod.MidnightType.Jafari
                ? sunset + timeDiff(sunset, fajr) / 2
                : sunset + timeDiff(sunset, sunrise) / 2;

        this.imsak = fixHour(imsak);
        this.fajr = fixHour(fajr);
        this.sunrise = fixHour(sunrise);
        this.dhuhr = fixHour(dhuhr);
        this.asr = fixHour(asr);
        this.sunset = fixHour(sunset);
        this.maghrib = fixHour(maghrib);
        this.isha = fixHour(isha);
        this.midnight = fixHour(midnight);
    }

    // default times
    private static final double DEFAULT_IMSAK = 5. / 24;
    private static final double DEFAULT_FAJR = 5. / 24;
    private static final double DEFAULT_SUNRISE = 6. / 24;
    private static final double DEFAULT_DHUHR = 12. / 24;
    private static final double DEFAULT_ASR = 13. / 24;
    private static final double DEFAULT_SUNSET = 18. / 24;
    private static final double DEFAULT_MAGHRIB = 18. / 24;
    private static final double DEFAULT_ISHA = 18. / 24;
    private static final CalculationMethod.MinuteOrAngleDouble DEFAULT_TIME_IMSAK = min(10);
    private static final CalculationMethod.MinuteOrAngleDouble DEFAULT_TIME_DHUHR = min(0);

    // compute mid-day time
    private static double midDay(double jdate, double time) {
        double eqt = sunPosition(jdate + time).equation;
        return fixHour(12 - eqt);
    }

    // compute the time at which sun reaches a specific angle below horizon
    private static double sunAngleTime(double jdate, CalculationMethod.MinuteOrAngleDouble angle, double time, boolean ccw, Coordinates coordinates) {
        // TODO: the below assert should be considered
        // if (angle.isMinute()) throw new IllegalArgumentException("angle argument must be degree, not minute!");
        double decl = sunPosition(jdate + time).declination;
        double noon = dtr(midDay(jdate, time));
        double t = Math.acos((-Math.sin(dtr(angle.value)) - Math.sin(decl)
                * Math.sin(dtr(coordinates.latitude)))
                / (Math.cos(decl) * Math.cos(dtr(coordinates.latitude)))) / 15.;
        return rtd(noon + (ccw ? -t : t));
    }

    private static double sunAngleTime(double jdate, CalculationMethod.MinuteOrAngleDouble angle, double time, Coordinates coordinates) {
        return sunAngleTime(jdate, angle, time, false, coordinates);
    }

    // compute asr time
    private static double asrTime(double jdate, double factor, double time, Coordinates coordinates) {
        double decl = sunPosition(jdate + time).declination;
        double angle = -Math.atan(1 / (factor + Math.tan(Math.abs(dtr(coordinates.latitude) - decl))));
        return sunAngleTime(jdate, deg(rtd(angle)), time, coordinates);
    }

    // compute declination angle of sun and equation of time
    // Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
    private static DeclEqt sunPosition(double jd) {
        double D = jd - 2451545d;
        double g = (357.529 + 0.98560028 * D) % 360;
        double q = (280.459 + 0.98564736 * D) % 360;
        double L = (q + 1.915 * Math.sin(dtr(g)) + 0.020 * Math.sin(dtr(2d * g))) % 360;

        // weird!
        // double R = 1.00014 - 0.01671 * Math.cos(dtr(g)) - 0.00014 *
        // Math.cos(dtr(2d * g));

        double e = 23.439 - 0.00000036 * D;

        double RA = rtd(Math.atan2(Math.cos(dtr(e)) * Math.sin(dtr(L)), Math.cos(dtr(L)))) / 15d;
        double eqt = q / 15d - fixHour(RA);
        double decl = Math.asin(Math.sin(dtr(e)) * Math.sin(dtr(L)));

        return new DeclEqt(decl, eqt);
    }

    // convert Gregorian date to Julian day
    // Ref: Astronomical Algorithms by Jean Meeus
    private static double julian(int year, int month, int day) {
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor((double) year / 100);
        double B = 2 - A + Math.floor(A / 4);

        return Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
    }

    // Section 2!! (Compute Prayer Time in JS code)
    //

    // return sun angle for sunset/sunrise
    private static CalculationMethod.MinuteOrAngleDouble riseSetAngle(Coordinates coordinates) {
        // var earthRad = 6371009; // in meters
        // var angle = DMath.arccos(earthRad/(earthRad+ elv));
        double angle = 0.0347 * Math.sqrt(Math.max(coordinates.elevation, 0)); // an approximation
        return deg(0.833 + angle);
    }

    // adjust a time for higher latitudes
    private static double adjustHLTime(HighLatitudesMethod highLatMethod,
                                       double time, double bbase, double angle,
                                       double night, boolean ccw) {
        double portion = nightPortion(highLatMethod, angle, night);
        double timeDiff = ccw ? timeDiff(time, bbase) : timeDiff(bbase, time);

        if (Double.isNaN(time) || timeDiff > portion)
            time = bbase + (ccw ? -portion : portion);
        return time;
    }

    private static double adjustHLTime(HighLatitudesMethod highLatMethod,
                                       double time, double bbase, double angle, double night) {
        return adjustHLTime(highLatMethod, time, bbase, angle, night, false);
    }

    // the night portion used for adjusting times in higher latitudes
    private static double nightPortion(HighLatitudesMethod highLatMethod, double angle, double night) {
        double portion = 1d / 2d;
        if (highLatMethod == HighLatitudesMethod.AngleBased) {
            portion = 1d / 60d * angle;
        }
        if (highLatMethod == HighLatitudesMethod.OneSeventh) {
            portion = 1d / 7d;
        }
        return portion * night;
    }

    // compute the difference between two times
    private static double timeDiff(double time1, double time2) {
        return fixHour(time2 - time1);
    }

    //
    // Misc Functions
    //
    //
    static double dtr(double d) {
        return (d * Math.PI) / 180d;
    }

    static double rtd(double r) {
        return (r * 180d) / Math.PI;
    }

    static double fixHour(double a) {
        double result = a % 24;
        return result < 0 ? 24 + result : result;
    }

    private static class DeclEqt {
        final double declination;
        final double equation;

        DeclEqt(double declination, double equation) {
            this.declination = declination;
            this.equation = equation;
        }
    }
}
