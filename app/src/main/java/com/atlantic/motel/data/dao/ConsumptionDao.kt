package com.atlantic.motel.data.dao

import androidx.room.*
import com.atlantic.motel.data.model.Consumption
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionDao {
    @Query("SELECT * FROM consumptions WHERE stayId = :stayId")
    fun getByStay(stayId: Long): Flow<List<Consumption>>

    @Query("SELECT * FROM consumptions WHERE stayId = :stayId")
    suspend fun getByStayList(stayId: Long): List<Consumption>

    @Insert
    suspend fun insert(consumption: Consumption): Long

    @Delete
    suspend fun delete(consumption: Consumption)

    @Query("UPDATE consumptions SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Query("SELECT COALESCE(SUM(quantity * unitPriceInCents), 0) FROM consumptions WHERE stayId = :stayId")
    suspend fun getTotalByStay(stayId: Long): Long
}
