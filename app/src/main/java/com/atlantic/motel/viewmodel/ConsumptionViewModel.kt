package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.data.model.Consumption
import com.atlantic.motel.data.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConsumptionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val consumptionDao = db.consumptionDao()
    private val productDao = db.productDao()

    private val _stayId = MutableStateFlow<Long?>(null)

    val products: StateFlow<List<Product>> = productDao.getActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consumptions: StateFlow<List<Consumption>> = _stayId.filterNotNull()
        .flatMapLatest { id -> consumptionDao.getByStay(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadStay(stayId: Long) {
        _stayId.value = stayId
    }

    fun addConsumption(product: Product, quantity: Int) {
        val stayId = _stayId.value ?: return
        viewModelScope.launch {
            consumptionDao.insert(
                Consumption(
                    stayId = stayId,
                    productId = product.id,
                    productName = product.name,
                    quantity = quantity,
                    unitPriceInCents = product.priceInCents
                )
            )
        }
    }

    fun removeConsumption(consumption: Consumption) {
        viewModelScope.launch {
            consumptionDao.delete(consumption)
        }
    }

    fun updateQuantity(consumption: Consumption, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                consumptionDao.delete(consumption)
            } else {
                consumptionDao.updateQuantity(consumption.id, newQuantity)
            }
        }
    }
}

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as AtlanticMotelApp).database
    private val productDao = db.productDao()

    val products: StateFlow<List<Product>> = productDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addProduct(name: String, priceInCents: Long) {
        viewModelScope.launch {
            productDao.insert(Product(name = name, priceInCents = priceInCents))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productDao.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.delete(product)
        }
    }
}
