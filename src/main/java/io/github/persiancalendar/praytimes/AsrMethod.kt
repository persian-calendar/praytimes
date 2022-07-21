package io.github.persiancalendar.praytimes

// Asr Juristic Methods
enum class AsrMethod(  // Hanafi
    // asr shadow factor
    val asrFactor: Double
) {
    Standard(1.0),  // Shafi`i, Maliki, Ja`fari, Hanbali
    Hanafi(2.0);
}