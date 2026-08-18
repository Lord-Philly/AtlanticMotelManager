package com.atlantic.motel

import android.app.Application
import com.atlantic.motel.data.database.DatabaseProvider
import com.atlantic.motel.data.model.User

class AtlanticMotelApp : Application() {
    val database by lazy { DatabaseProvider.getDatabase(this) }

    private var currentUser: User? = null

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun getCurrentUser(): User? = currentUser

    fun logout() {
        currentUser = null
    }

    fun isAdmin(): Boolean = currentUser?.role == com.atlantic.motel.data.model.UserRole.ADMIN

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AtlanticMotelApp
            private set
    }
}
