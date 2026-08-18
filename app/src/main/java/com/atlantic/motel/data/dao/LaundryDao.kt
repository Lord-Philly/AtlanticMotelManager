package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Laundry
import com.atlantic.motel.data.model.LaundryStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryDao {
    @Query("SELECT * FROM laundry ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Laundry>>

    @Query("SELECT * FROM laundry WHERE status = :status ORDER BY timestamp DESC")
    fun getByStatus(status: LaundryStatus): Flow<List<Laundry>>

    @Query("SELECT COUNT(*) FROM laundry WHERE status = :status")
    fun countByStatus(status: LaundryStatus): Flow<Int>

    @Query("SELECT SUM(quantity) FROM laundry WHERE status = :status")
    fun sumQuantityByStatus(status: LaundryStatus): Flow<Int?>

    @Insert
    suspend fun insert(laundry: Laundry): Long

    @Update
    suspend fun update(laundry: Laundry)

    @Delete
    suspend fun delete(laundry: Laundry)

    @Query("DELETE FROM laundry")
    suspend fun deleteAll()
}
