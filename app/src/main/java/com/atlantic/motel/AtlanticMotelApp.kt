package com.atlantic.motel

import android.app.Application
import com.atlantic.motel.data.database.DatabaseProvider

class AtlanticMotelApp : Application() {
    val database by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AtlanticMotelApp
            private set
    }
}
