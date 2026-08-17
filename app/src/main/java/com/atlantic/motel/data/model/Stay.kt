package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stays",
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
data class Stay(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val apartmentId: Long,
    val guestName: String = "",
    val startTime: Long,
    val endTime: Long? = null,
    val isActive: Boolean = true
)
