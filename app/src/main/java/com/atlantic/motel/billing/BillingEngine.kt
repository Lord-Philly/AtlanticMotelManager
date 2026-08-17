package com.atlantic.motel.billing

import java.util.concurrent.TimeUnit

object BillingEngine {

    data class BillingResult(
        val amountInCents: Long,
        val description: String
    )

    data class RateEntry(
        val minutes: Int,
        val amountInCents: Long,
        val label: String
    )

    private val rateTable = listOf(
        RateEntry(60, 10000, "1h"),
        RateEntry(90, 15000, "1:30"),
        RateEntry(120, 20000, "2h"),
        RateEntry(150, 25000, "2:30"),
        RateEntry(180, 30000, "3h"),
        RateEntry(210, 35000, "3:30"),
        RateEntry(240, 40000, "4h")
    )

    private const val OVERNIGHT_RATE_CENTS = 40000L
    private const val OVERNIGHT_MINUTES = 240

    fun calculateStayAmount(startTimeMillis: Long, endTimeMillis: Long): BillingResult {
        val durationMillis = endTimeMillis - startTimeMillis
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()

        if (durationMinutes <= 0) {
            return BillingResult(0, "Tempo invalido")
        }

        if (durationMinutes <= OVERNIGHT_MINUTES) {
            val entry = findRateEntry(durationMinutes)
            return BillingResult(entry.amountInCents, entry.label)
        }

        val baseStay = (durationMinutes / 1440)
        val remainderMinutes = durationMinutes % 1440
        val baseAmount = baseStay * OVERNIGHT_RATE_CENTS

        if (remainderMinutes == 0) {
            val label = if (baseStay == 1) "Pernoite" else "${baseStay}x Pernoite"
            return BillingResult(baseAmount, label)
        }

        val extraEntry = findRateEntry(remainderMinutes)
        val total = baseAmount + extraEntry.amountInCents
        val label = if (baseStay > 0) {
            "${baseStay}x Pernoite + ${extraEntry.label}"
        } else {
            extraEntry.label
        }

        return BillingResult(total, label)
    }

    private fun findRateEntry(durationMinutes: Int): RateEntry {
        for (entry in rateTable) {
            if (durationMinutes <= entry.minutes) {
                return entry
            }
        }
        return rateTable.last()
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
}
