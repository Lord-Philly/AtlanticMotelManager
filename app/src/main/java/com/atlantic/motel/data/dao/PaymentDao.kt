package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Payment>>

    @Query("SELECT * FROM payments ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE stayId = :stayId")
    suspend fun getByStay(stayId: Long): Payment?

    @Insert
    suspend fun insert(payment: Payment): Long

    @Query("SELECT COALESCE(SUM(totalInCents), 0) FROM payments WHERE timestamp BETWEEN :start AND :end")
    suspend fun getTotalBetween(start: Long, end: Long): Long

    @Query("SELECT * FROM payments WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    suspend fun getAllBetweenSync(start: Long, end: Long): List<Payment>
}
