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
        Reservation::class,
        User::class,
        Laundry::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao
    abstract fun stayDao(): StayDao
    abstract fun productDao(): ProductDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reservationDao(): ReservationDao
    abstract fun userDao(): UserDao
    abstract fun laundryDao(): LaundryDao

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
                    .fallbackToDestructiveMigration()
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

            val userDao = db.userDao()
            if (userDao.count() == 0) {
                userDao.insert(
                    User(
                        username = "admin",
                        password = "admin",
                        displayName = "Administrador",
                        role = UserRole.ADMIN,
                        gender = UserGender.MASCULINO
                    )
                )
                userDao.insert(
                    User(
                        username = "kesia",
                        password = "1234",
                        displayName = "Kesia",
                        role = UserRole.FUNCIONARIO,
                        gender = UserGender.FEMININO
                    )
                )
                userDao.insert(
                    User(
                        username = "reginaldo",
                        password = "1234",
                        displayName = "Reginaldo",
                        role = UserRole.FUNCIONARIO,
                        gender = UserGender.MASCULINO
                    )
                )
            }
        }
    }
}
