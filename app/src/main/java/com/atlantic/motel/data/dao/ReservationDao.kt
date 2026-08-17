package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Reservation
import com.atlantic.motel.data.model.ReservationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations ORDER BY date ASC, time ASC")
    fun getAll(): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE status != :excludeStatus ORDER BY date ASC, time ASC")
    fun getActive(excludeStatus: ReservationStatus = ReservationStatus.CANCELADA): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE apartmentId = :apartmentId AND date = :date AND status != :excludeStatus")
    suspend fun getByApartmentAndDate(apartmentId: Long, date: String, excludeStatus: ReservationStatus = ReservationStatus.CANCELADA): List<Reservation>

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getById(id: Long): Reservation?

    @Insert
    suspend fun insert(reservation: Reservation): Long

    @Update
    suspend fun update(reservation: Reservation)

    @Query("UPDATE reservations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ReservationStatus)
}
