package io.github.persiancalendar.praytimes;

public enum CalculationMethod {
    // Muslim World League
    MWL(deg(18), deg(17)),
    // Islamic Society of North America (ISNA)
    ISNA(deg(15), deg(15)),
    // Egyptian General Authority of Survey
    Egypt(deg(19.5), deg(17.5)),
    // Umm Al-Qura University, Makkah
    Makkah(deg(18.5), min(90)),
    // University of Islamic Sciences, Karachi
    Karachi(deg(18), deg(18)),
    // Institute of Geophysics, University of Tehran
    Tehran(deg(17.7), deg(14), deg(4.5), MidnightType.Jafari),
    // Shia Ithna-Ashari, Leva Institute, Qum
    Jafari(deg(16), deg(14), deg(4), MidnightType.Jafari);

    final MinuteOrAngleDouble fajr;
    final MinuteOrAngleDouble isha;
    final MinuteOrAngleDouble maghrib;
    final MidnightType midnight;

    public final boolean isJafari;

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha, MinuteOrAngleDouble maghrib,
                      MidnightType midnight) {
        this.fajr = fajr;
        this.isha = isha;
        this.maghrib = maghrib;
        this.midnight = midnight;
        this.isJafari = midnight == MidnightType.Jafari;
    }

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha, MinuteOrAngleDouble maghrib) {
        this(fajr, isha, maghrib, MidnightType.Standard);
    }

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha) {
        this(fajr, isha, min(0));
    }

    static MinuteOrAngleDouble deg(int value) {
        return deg((double) value);
    }

    static MinuteOrAngleDouble min(int value) {
        return new MinuteOrAngleDouble(value, true);
    }

    static MinuteOrAngleDouble deg(double value) {
        return new MinuteOrAngleDouble(value, false);
    }

    // Midnight Mode
    enum MidnightType {
        Standard, // Mid Sunset to Sunrise
        Jafari // Mid Sunset to Fajr
    }

    static class MinuteOrAngleDouble {

        final boolean isMinutes;
        final double value;

        MinuteOrAngleDouble(double value, boolean isMinutes) {
            this.value = value;
            this.isMinutes = isMinutes;
        }
    }
}
