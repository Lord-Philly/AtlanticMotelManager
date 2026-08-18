package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY displayName ASC")
    fun getAllActive(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username AND password = :password AND isActive = 1 LIMIT 1")
    suspend fun authenticate(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): User?

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Query("UPDATE users SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}
