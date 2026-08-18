package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.data.database.AppDatabase
import com.atlantic.motel.data.model.Laundry
import com.atlantic.motel.data.model.LaundryItem
import com.atlantic.motel.data.model.LaundryStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LaundryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val laundryDao = db.laundryDao()

    val allLaundry = laundryDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fronhas = allLaundry.map { list -> list.filter { it.item == LaundryItem.FRONHA } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lencois = allLaundry.map { list -> list.filter { it.item == LaundryItem.LENCOL } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sujoCount = laundryDao.sumQuantityByStatus(LaundryStatus.SUJO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lavandoCount = laundryDao.sumQuantityByStatus(LaundryStatus.LAVANDO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val limpoCount = laundryDao.sumQuantityByStatus(LaundryStatus.LIMPO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addItem(item: LaundryItem, quantity: Int, apartmentNumber: String) {
        viewModelScope.launch {
            laundryDao.insert(
                Laundry(
                    item = item,
                    quantity = quantity,
                    apartmentNumber = apartmentNumber
                )
            )
        }
    }

    fun updateStatus(laundry: Laundry, newStatus: LaundryStatus) {
        viewModelScope.launch {
            laundryDao.update(laundry.copy(status = newStatus))
        }
    }

    fun updateQuantity(laundry: Laundry, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                laundryDao.update(laundry.copy(quantity = newQuantity))
            }
        }
    }

    fun removeItem(laundry: Laundry) {
        viewModelScope.launch { laundryDao.delete(laundry) }
    }

    fun clearAll() {
        viewModelScope.launch { laundryDao.deleteAll() }
    }
}
