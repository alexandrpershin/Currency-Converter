package com.alexandrpershin.currencyconverter

import android.app.AlertDialog
import android.app.Application
import com.alexandrpershin.currencyconverter.di.Injector

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.initialiseAppComponent(this)
    }
}

