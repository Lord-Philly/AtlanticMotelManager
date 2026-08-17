package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consumptions",
    foreignKeys = [
        ForeignKey(
            entity = Stay::class,
            parentColumns = ["id"],
            childColumns = ["stayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stayId"), Index("productId")]
)
data class Consumption(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stayId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPriceInCents: Long
)
