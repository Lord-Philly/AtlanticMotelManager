package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState
import kotlinx.coroutines.flow.Flow

@Dao
interface ApartmentDao {
    @Query("SELECT * FROM apartments ORDER BY number ASC")
    fun getAll(): Flow<List<Apartment>>

    @Query("SELECT * FROM apartments WHERE id = :id")
    suspend fun getById(id: Long): Apartment?

    @Query("SELECT * FROM apartments WHERE state = :state")
    fun getByState(state: ApartmentState): Flow<List<Apartment>>

    @Insert
    suspend fun insert(apartment: Apartment): Long

    @Update
    suspend fun update(apartment: Apartment)

    @Delete
    suspend fun delete(apartment: Apartment)

    @Query("UPDATE apartments SET state = :state WHERE id = :id")
    suspend fun updateState(id: Long, state: ApartmentState)

    @Query("UPDATE apartments SET state = :state, maintenanceNote = :note WHERE id = :id")
    suspend fun updateStateWithNote(id: Long, state: ApartmentState, note: String)
}
