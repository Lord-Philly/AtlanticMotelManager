package com.atlantic.motel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN, FUNCIONARIO
}

enum class UserGender {
    MASCULINO, FEMININO
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val displayName: String,
    val role: UserRole,
    val gender: UserGender = UserGender.MASCULINO,
    val isActive: Boolean = true
)
