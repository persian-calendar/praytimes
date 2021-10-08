package io.github.persiancalendar.praytimes;

// Asr Juristic Methods
public enum AsrMethod {
    Standard(1d), // Shafi`i, Maliki, Ja`fari, Hanbali
    Hanafi(2d); // Hanafi

    // asr shadow factor
    final double asrFactor;

    AsrMethod(double factor) {
        asrFactor = factor;
    }
}
