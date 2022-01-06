package com.app.todotoday.sharedPreferences

import android.app.Application

class SharedData:Application() {
    companion object{
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}