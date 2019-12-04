package com.alexandrpershin.currencyconverter.di.module

import android.content.Context
import com.alexandrpershin.currencyconverter.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: MyApplication) {

    @Singleton
    @Provides
    fun provideContext(): Context = application

}