package io.github.persiancalendar.praytimes;

public class PrayTimes {
    private final double imsak, fajr, sunrise, dhuhr, asr, sunset, maghrib, isha, midnight;

    PrayTimes(double imsak, double fajr, double sunrise, double dhuhr,
              double asr, double sunset, double maghrib, double isha, double midnight) {
        this.imsak = imsak;
        this.fajr = fajr;
        this.sunrise = sunrise;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.sunset = sunset;
        this.maghrib = maghrib;
        this.isha = isha;
        this.midnight = midnight;
    }

    public Clock getImsak() {
        return Clock.fromDouble(imsak);
    }

    public Clock getFajr() {
        return Clock.fromDouble(fajr);
    }

    public Clock getSunrise() {
        return Clock.fromDouble(sunrise);
    }

    public Clock getDhuhr() {
        return Clock.fromDouble(dhuhr);
    }

    public Clock getAsr() {
        return Clock.fromDouble(asr);
    }

    public Clock getSunset() {
        return Clock.fromDouble(sunset);
    }

    public Clock getMaghrib() {
        return Clock.fromDouble(maghrib);
    }

    public Clock getIsha() {
        return Clock.fromDouble(isha);
    }

    public Clock getMidnight() {
        return Clock.fromDouble(midnight);
    }
}
