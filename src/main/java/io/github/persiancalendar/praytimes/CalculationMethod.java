package io.github.persiancalendar.praytimes;

import static io.github.persiancalendar.praytimes.Utils.deg;
import static io.github.persiancalendar.praytimes.Utils.min;

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

    private final MinuteOrAngleDouble fajr;
    private final MinuteOrAngleDouble isha;
    private final MinuteOrAngleDouble maghrib;
    private final MidnightType midnight;

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha, MinuteOrAngleDouble maghrib,
                      MidnightType midnight) {
        this.fajr = fajr;
        this.isha = isha;
        this.maghrib = maghrib == null ? min(0) : maghrib;
        this.midnight = midnight == null ? MidnightType.Standard : midnight;
    }

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha, MinuteOrAngleDouble maghrib) {
        this(fajr, isha, maghrib, null);
    }

    CalculationMethod(MinuteOrAngleDouble fajr, MinuteOrAngleDouble isha) {
        this(fajr, isha, null);
    }

    public MinuteOrAngleDouble getFajr() {
        return fajr;
    }

    public MinuteOrAngleDouble getIsha() {
        return isha;
    }

    public MinuteOrAngleDouble getMaghrib() {
        return maghrib;
    }

    public MidnightType getMidnight() {
        return midnight;
    }

    // Midnight Mode
    public enum MidnightType {
        Standard, // Mid Sunset to Sunrise
        Jafari // Mid Sunset to Fajr
    }

    // Asr Juristic Methods
    public enum AsrJuristics {
        Standard(1d), // Shafi`i, Maliki, Ja`fari, Hanbali
        Hanafi(2d); // Hanafi

        // asr shadow factor
        public final double asrFactor;

        AsrJuristics(double factor) {
            asrFactor = factor;
        }
    }

    // Adjust Methods for Higher Latitudes
    public enum HighLatMethods {
        NightMiddle, // middle of night
        AngleBased, // angle/60th of night
        OneSeventh, // 1/7th of night
        None // No adjustment
    }
}
