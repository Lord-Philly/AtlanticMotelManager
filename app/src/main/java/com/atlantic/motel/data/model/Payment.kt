package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentMethod {
    DINHEIRO, PIX, CARTAO
}

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stayId: Long,
    val apartmentNumber: String,
    val stayAmountInCents: Long,
    val consumptionAmountInCents: Long,
    val totalInCents: Long,
    val paymentMethod: PaymentMethod,
    val timestamp: Long
)
