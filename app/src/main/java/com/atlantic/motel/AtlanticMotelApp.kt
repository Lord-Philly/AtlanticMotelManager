package com.atlantic.motel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.atlantic.motel.data.database.DatabaseProvider
import com.atlantic.motel.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AtlanticMotelApp : Application() {
    val database by lazy { DatabaseProvider.getDatabase(this) }
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var currentUser by mutableStateOf<User?>(null)
        private set

    fun loginAs(user: User) {
        currentUser = user
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putLong(KEY_USER_ID, user.id)
            .apply()
    }

    fun logout() {
        currentUser = null
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .remove(KEY_USER_ID)
            .apply()
    }

    fun isAdmin(): Boolean = currentUser?.role == com.atlantic.motel.data.model.UserRole.ADMIN

    private fun restoreSession() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val userId = prefs.getLong(KEY_USER_ID, -1)
        if (userId > 0) {
            appScope.launch {
                val user = database.userDao().getById(userId)
                if (user != null && user.isActive) {
                    currentUser = user
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        restoreSession()
    }

    companion object {
        lateinit var instance: AtlanticMotelApp
            private set

        private const val PREFS_NAME = "session"
        private const val KEY_USER_ID = "userId"
    }
}
