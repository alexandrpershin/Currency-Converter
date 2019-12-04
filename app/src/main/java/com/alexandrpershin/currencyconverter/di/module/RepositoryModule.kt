package com.alexandrpershin.currencyconverter.di.module

import android.content.Context
import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideCurrencyRateApiService(context: Context): CurrencyRateApiService {
        return CurrencyRateApiService.create(context)
    }

    @Singleton
    @Provides
    fun provideCurrencyRateRepository(currencyRateApiService: CurrencyRateApiService): CurrencyRateRepository {
        return CurrencyRateRepository(currencyRateApiService)
    }

}