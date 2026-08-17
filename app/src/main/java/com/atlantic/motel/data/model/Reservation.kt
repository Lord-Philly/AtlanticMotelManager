package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ReservationStatus {
    PENDENTE, CONFIRMADA, CANCELADA, CONCLUIDA
}

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = Apartment::class,
            parentColumns = ["id"],
            childColumns = ["apartmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("apartmentId")]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val apartmentId: Long,
    val apartmentNumber: String,
    val guestName: String,
    val date: String,
    val time: String,
    val notes: String = "",
    val status: ReservationStatus = ReservationStatus.PENDENTE,
    val createdAt: Long
)
