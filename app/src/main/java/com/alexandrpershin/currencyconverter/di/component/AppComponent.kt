package com.alexandrpershin.currencyconverter.di.component

import android.content.Context
import com.alexandrpershin.currencyconverter.MyApplication
import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.di.module.AppModule
import com.alexandrpershin.currencyconverter.di.module.RepositoryModule
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RepositoryModule::class])
interface AppComponent {

    fun inject(application: MyApplication)

    fun provideContext(): Context

    fun provideCurrencyRateApiService(): CurrencyRateApiService

    fun provideCurrencyRateRepository(): CurrencyRateRepository
}