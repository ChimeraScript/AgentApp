package com.app.todotoday.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {

    private val SHARED_NAME = "UserPref"
    private val SHARED_USER_NAME = "username"
    private val SHARED_EMAIL = "email"
    private val SHARED_PROVIDER = "provider"
    private val SHARED_DASHBOARD = "dashboard"

    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_NAME, 0)

    //clear SharedPreferences
    fun clearAll(){
        storage.edit().clear().apply()
    }

    // Users's name
    fun saveName(name:String){
        storage.edit().putString(SHARED_USER_NAME, name).apply()
    }
    fun getName():String{
        return storage.getString(SHARED_USER_NAME, "")!!
    }

    // User's Email
    fun saveEmail(email:String){
        storage.edit().putString(SHARED_EMAIL, email).apply()
    }
    fun getEmail():String{
        return storage.getString(SHARED_EMAIL, "")!!
    }

    // Users's Provider
    fun saveProviderName(provider:String){
        storage.edit().putString(SHARED_PROVIDER, provider).apply()
    }
    fun getProvider():String{
        return storage.getString(SHARED_PROVIDER, "")!!
    }
}