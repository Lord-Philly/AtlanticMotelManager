package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ApartmentState {
    LIVRE, OCUPADO, LIMPEZA, MANUTENCAO
}

@Entity(tableName = "apartments")
data class Apartment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val number: String,
    val state: ApartmentState = ApartmentState.LIVRE,
    val maintenanceNote: String = ""
)
