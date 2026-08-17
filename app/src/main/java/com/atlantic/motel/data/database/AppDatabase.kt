package com.atlantic.motel.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atlantic.motel.data.dao.*
import com.atlantic.motel.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Apartment::class,
        Stay::class,
        Product::class,
        Consumption::class,
        Payment::class,
        Reservation::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao
    abstract fun stayDao(): StayDao
    abstract fun productDao(): ProductDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atlantic_motel_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val apartmentDao = db.apartmentDao()
            val numbers = listOf("21", "22", "23", "24")
            numbers.forEach { number ->
                apartmentDao.insert(Apartment(number = number))
            }
        }
    }
}
