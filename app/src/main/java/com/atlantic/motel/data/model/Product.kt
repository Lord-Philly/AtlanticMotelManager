package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProductCategory {
    CERVEJA,
    DRINK,
    COMBO,
    GERAL
}

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val priceInCents: Long,
    val category: ProductCategory = ProductCategory.GERAL,
    val isActive: Boolean = true
)
