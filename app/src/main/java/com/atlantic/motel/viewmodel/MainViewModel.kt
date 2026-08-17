package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ApartmentUiState(
    val apartment: Apartment,
    val duration: String? = null,
    val amount: String? = null,
    val guestName: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val apartmentDao = db.apartmentDao()
    private val stayDao = db.stayDao()

    private val _currentMillis = MutableStateFlow(System.currentTimeMillis())
    val currentMillis: StateFlow<Long> = _currentMillis

    init {
        viewModelScope.launch {
            while (true) {
                _currentMillis.value = System.currentTimeMillis()
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    val apartments: StateFlow<List<ApartmentUiState>> = combine(
        apartmentDao.getAll(),
        stayDao.getActiveStays(),
        _currentMillis
    ) { apartments, activeStays, currentMillis ->
        val staysByApartment = activeStays.associateBy { it.apartmentId }

        apartments.map { apartment ->
            val stay = staysByApartment[apartment.id]
            if (stay != null && apartment.state == ApartmentState.OCUPADO) {
                val duration = BillingEngine.formatDuration(stay.startTime, currentMillis)
                val billing = BillingEngine.calculateStayAmount(stay.startTime, currentMillis)
                ApartmentUiState(
                    apartment = apartment,
                    duration = duration,
                    amount = BillingEngine.formatCurrency(billing.amountInCents),
                    guestName = stay.guestName.ifBlank { null }
                )
            } else {
                ApartmentUiState(apartment = apartment)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun startStay(apartmentId: Long, guestName: String) {
        viewModelScope.launch {
            stayDao.insert(
                com.atlantic.motel.data.model.Stay(
                    apartmentId = apartmentId,
                    guestName = guestName,
                    startTime = System.currentTimeMillis()
                )
            )
            apartmentDao.updateState(apartmentId, ApartmentState.OCUPADO)
        }
    }

    fun endStayAndStartCleaning(apartmentId: Long) {
        viewModelScope.launch {
            val stay = stayDao.getActiveStayByApartment(apartmentId)
            if (stay != null) {
                stayDao.endStay(stay.id, System.currentTimeMillis())
            }
            apartmentDao.updateState(apartmentId, ApartmentState.LIMPEZA)
        }
    }

    fun markCleaned(apartmentId: Long) {
        viewModelScope.launch {
            apartmentDao.updateState(apartmentId, ApartmentState.LIVRE)
        }
    }

    fun setMaintenance(apartmentId: Long, note: String) {
        viewModelScope.launch {
            apartmentDao.updateStateWithNote(apartmentId, ApartmentState.MANUTENCAO, note)
        }
    }

    fun clearMaintenance(apartmentId: Long) {
        viewModelScope.launch {
            apartmentDao.updateState(apartmentId, ApartmentState.LIVRE)
            apartmentDao.updateStateWithNote(apartmentId, ApartmentState.LIVRE, "")
        }
    }

    fun addApartment(number: String) {
        viewModelScope.launch {
            apartmentDao.insert(Apartment(number = number))
        }
    }
}
