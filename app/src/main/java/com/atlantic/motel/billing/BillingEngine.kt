package com.atlantic.motel.billing

import java.util.concurrent.TimeUnit

object BillingEngine {

    data class BillingResult(
        val amountInCents: Long,
        val description: String
    )

    private const val RATE_PER_HOUR_CENTS = 10000L
    private const val RATE_PER_30MIN_CENTS = 5000L
    private const val FIRST_HOUR_TOLERANCE_MINUTES = 10

    fun calculateStayAmount(startTimeMillis: Long, endTimeMillis: Long): BillingResult {
        val durationMillis = endTimeMillis - startTimeMillis
        val durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)

        if (durationSeconds <= 0) {
            return BillingResult(0, "Tempo invalido")
        }

        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()

        if (durationMinutes < 60) {
            return BillingResult(RATE_PER_HOUR_CENTS, "1h (minimo)")
        }

        val billableMinutes = durationMinutes - FIRST_HOUR_TOLERANCE_MINUTES
        val extraMinutes = billableMinutes - 60
        val extraBlocks = (extraMinutes + 29) / 30
        val totalCents = RATE_PER_HOUR_CENTS + (extraBlocks * RATE_PER_30MIN_CENTS)

        val hours = durationMinutes / 60
        val mins = durationMinutes % 60
        val desc = if (mins > 0) "${hours}h${mins}min" else "${hours}h"

        return BillingResult(totalCents, desc)
    }

    fun formatCurrency(amountInCents: Long): String {
        val reais = amountInCents / 100
        val centavos = amountInCents % 100
        return "R$ %d,%02d".format(reais, centavos)
    }

    fun formatDuration(startTimeMillis: Long, endTimeMillis: Long): String {
        val durationMillis = endTimeMillis - startTimeMillis
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "%dh %02dmin".format(hours, minutes)
    }

    fun formatDurationHMS(startTimeMillis: Long, endTimeMillis: Long): String {
        val durationMillis = endTimeMillis - startTimeMillis
        if (durationMillis < 0) return "00:00:00"
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
}
