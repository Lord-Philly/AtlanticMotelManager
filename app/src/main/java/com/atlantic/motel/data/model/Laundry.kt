package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LaundryItem {
    FRONHA,
    LENCOL,
    TOALHA,
    OUTRO
}

enum class LaundryStatus {
    SUJO,
    LAVANDO,
    LIMPO
}

@Entity(tableName = "laundry")
data class Laundry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val item: LaundryItem,
    val quantity: Int = 1,
    val status: LaundryStatus = LaundryStatus.SUJO,
    val apartmentNumber: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
