package io.github.persiancalendar.praytimes;

// Adjust Methods for Higher Latitudes
public enum HighLatitudeMethod {
    NightMiddle, // middle of night, the default
    AngleBased, // angle/60th of night
    OneSeventh, // 1/7th of night
    None // No adjustment
}
