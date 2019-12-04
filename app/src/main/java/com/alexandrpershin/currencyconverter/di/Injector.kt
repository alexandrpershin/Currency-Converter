package com.alexandrpershin.currencyconverter.di

import com.alexandrpershin.currencyconverter.MyApplication
import com.alexandrpershin.currencyconverter.di.component.AppComponent
import com.alexandrpershin.currencyconverter.di.component.DaggerAppComponent
import com.alexandrpershin.currencyconverter.di.component.DaggerMainActivityComponent
import com.alexandrpershin.currencyconverter.di.component.MainActivityComponent
import com.alexandrpershin.currencyconverter.di.module.AppModule
import com.alexandrpershin.currencyconverter.di.module.MainActivityModule
import com.alexandrpershin.currencyconverter.mainactivity.MainActivity

object Injector {

    private var appComponent: AppComponent? = null

    private var mainActivityComponent: MainActivityComponent? = null

    fun initialiseAppComponent(application: MyApplication) {
        if (appComponent == null) {
            appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(application))
                .build()
        }

        appComponent?.inject(application)
    }

    fun initialiseMainActivityComponent(activity: MainActivity) {
        if (mainActivityComponent == null) {
            mainActivityComponent = DaggerMainActivityComponent
                .builder()
                .appComponent(appComponent)
                .mainActivityModule(MainActivityModule(activity))
                .build()
        }

        mainActivityComponent?.inject(activity)
    }

    fun releaseMainActivityComponent() {
        mainActivityComponent = null
    }

}