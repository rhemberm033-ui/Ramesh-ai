package com.rameshai

import android.app.Application

class RameshApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Hook for future initialization: crash reporting, work managers, etc.
        // Deliberately does NOT initialize any AI/search SDK with a key here —
        // all of that lives on the backend (see network/NetworkModule.kt).
    }
}
