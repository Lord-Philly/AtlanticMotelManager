package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Stay
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Query("SELECT * FROM stays ORDER BY startTime DESC")
    fun getAll(): Flow<List<Stay>>

    @Query("SELECT * FROM stays WHERE isActive = 1")
    fun getActiveStays(): Flow<List<Stay>>

    @Query("SELECT * FROM stays WHERE apartmentId = :apartmentId AND isActive = 1 LIMIT 1")
    suspend fun getActiveStayByApartment(apartmentId: Long): Stay?

    @Query("SELECT * FROM stays WHERE apartmentId = :apartmentId AND isActive = 1 LIMIT 1")
    fun getActiveStayByApartmentFlow(apartmentId: Long): Flow<Stay?>

    @Query("SELECT * FROM stays WHERE id = :id")
    suspend fun getById(id: Long): Stay?

    @Insert
    suspend fun insert(stay: Stay): Long

    @Update
    suspend fun update(stay: Stay)

    @Query("UPDATE stays SET isActive = 0, endTime = :endTime WHERE id = :id")
    suspend fun endStay(id: Long, endTime: Long)

    @Query("SELECT * FROM stays ORDER BY startTime DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<Stay>>
}
