package com.atlantic.motel.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StayDetailState(
    val stay: Stay? = null,
    val apartment: Apartment? = null,
    val consumptions: List<Consumption> = emptyList(),
    val consumptionTotal: Long = 0,
    val duration: String = "",
    val durationHMS: String = "",
    val stayAmount: Long = 0,
    val total: Long = 0
)

class StayViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val stayDao = db.stayDao()
    private val apartmentDao = db.apartmentDao()
    private val consumptionDao = db.consumptionDao()
    private val paymentDao = db.paymentDao()

    private val _tick = MutableStateFlow(0L)
    val tick: StateFlow<Long> = _tick

    init {
        viewModelScope.launch {
            while (true) {
                _tick.value = SystemClock.elapsedRealtime()
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    private val _apartmentId = MutableStateFlow<Long?>(null)

    val stayDetail: StateFlow<StayDetailState> = _apartmentId.filterNotNull()
        .flatMapLatest { apartmentId ->
            stayDao.getActiveStayByApartmentFlow(apartmentId).flatMapLatest { stay ->
                if (stay != null) {
                    combine(
                        consumptionDao.getByStay(stay.id),
                        _tick
                    ) { consumptions, _ ->
                        val now = System.currentTimeMillis()
                        val apartment = apartmentDao.getById(stay.apartmentId)
                        val consumptionTotal = consumptions.sumOf { it.quantity * it.unitPriceInCents }
                        val duration = BillingEngine.formatDuration(stay.startTime, now)
                        val durationHMS = BillingEngine.formatDurationHMS(stay.startTime, now)
                        val stayAmount = BillingEngine.calculateStayAmount(stay.startTime, now).amountInCents
                        StayDetailState(
                            stay = stay,
                            apartment = apartment,
                            consumptions = consumptions,
                            consumptionTotal = consumptionTotal,
                            duration = duration,
                            durationHMS = durationHMS,
                            stayAmount = stayAmount,
                            total = stayAmount + consumptionTotal
                        )
                    }
                } else {
                    flowOf(StayDetailState())
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StayDetailState()
        )

    fun loadByApartment(apartmentId: Long) {
        _apartmentId.value = apartmentId
    }

    fun checkout(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            val state = stayDetail.value
            val stay = state.stay ?: return@launch
            val apartment = state.apartment ?: return@launch

            stayDao.endStay(stay.id, System.currentTimeMillis())
            apartmentDao.updateState(stay.apartmentId, ApartmentState.LIMPEZA)

            paymentDao.insert(
                Payment(
                    stayId = stay.id,
                    apartmentNumber = apartment.number,
                    stayAmountInCents = state.stayAmount,
                    consumptionAmountInCents = state.consumptionTotal,
                    totalInCents = state.total,
                    paymentMethod = paymentMethod,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
