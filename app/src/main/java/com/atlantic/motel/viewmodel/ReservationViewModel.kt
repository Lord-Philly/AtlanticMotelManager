package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val reservationDao = db.reservationDao()
    private val apartmentDao = db.apartmentDao()

    val reservations: StateFlow<List<Reservation>> = reservationDao.getActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val apartments: StateFlow<List<Apartment>> = apartmentDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReservation(
        apartmentId: Long,
        apartmentNumber: String,
        guestName: String,
        date: String,
        time: String,
        notes: String
    ) {
        viewModelScope.launch {
            val conflicts = reservationDao.getByApartmentAndDate(apartmentId, date)
            val hasConflict = conflicts.any { existing ->
                existing.time == time || areTimesOverlapping(existing.time, time)
            }
            if (!hasConflict) {
                reservationDao.insert(
                    Reservation(
                        apartmentId = apartmentId,
                        apartmentNumber = apartmentNumber,
                        guestName = guestName,
                        date = date,
                        time = time,
                        notes = notes,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun cancelReservation(id: Long) {
        viewModelScope.launch {
            reservationDao.updateStatus(id, ReservationStatus.CANCELADA)
        }
    }

    fun confirmReservation(id: Long) {
        viewModelScope.launch {
            reservationDao.updateStatus(id, ReservationStatus.CONFIRMADA)
        }
    }

    fun completeReservation(id: Long) {
        viewModelScope.launch {
            reservationDao.updateStatus(id, ReservationStatus.CONCLUIDA)
        }
    }

    private fun areTimesOverlapping(time1: String, time2: String): Boolean {
        try {
            val parts1 = time1.split(":").map { it.toInt() }
            val parts2 = time2.split(":").map { it.toInt() }
            val mins1 = parts1[0] * 60 + parts1[1]
            val mins2 = parts2[0] * 60 + parts2[1]
            return kotlin.math.abs(mins1 - mins2) < 60
        } catch (e: Exception) {
            return false
        }
    }
}

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val paymentDao = db.paymentDao()
    private val stayDao = db.stayDao()

    val recentPayments: StateFlow<List<Payment>> = paymentDao.getRecent(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
