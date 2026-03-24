package com.example.grocerylist

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class GroceryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
