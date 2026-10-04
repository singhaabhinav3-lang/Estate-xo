package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val rateFromUsd: Double,
    val flag: String,
    val fullName: String
) {
    USD("USD", "$", 1.0, "🇺🇸", "US Dollar"),
    EUR("EUR", "€", 0.92, "🇪🇺", "Euro"),
    GBP("GBP", "£", 0.78, "🇬🇧", "British Pound"),
    AED("AED", "AED ", 3.67, "🇦🇪", "UAE Dirham"),
    JPY("JPY", "¥", 152.4, "🇯🇵", "Japanese Yen"),
    SGD("SGD", "S$", 1.34, "🇸🇬", "Singapore Dollar"),
    CHF("CHF", "CHF ", 0.88, "🇨🇭", "Swiss Franc");

    fun convertFromUsd(amountInUsd: Double): Double = amountInUsd * rateFromUsd

    fun format(amountInUsd: Double): String {
        val converted = convertFromUsd(amountInUsd)
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.maximumFractionDigits = 0
        return when (this) {
            USD, GBP -> "$symbol${format.format(converted)}"
            EUR -> "€${format.format(converted)}"
            AED -> "${format.format(converted)} AED"
            JPY -> "¥${format.format(converted)}"
            SGD -> "S$${format.format(converted)}"
            CHF -> "${format.format(converted)} CHF"
        }
    }
}
