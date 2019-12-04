package com.alexandrpershin.currencyconverter.di.module

import com.alexandrpershin.currencyconverter.di.scope.MainActivityScope
import com.alexandrpershin.currencyconverter.mainactivity.MainActivity
import com.alexandrpershin.currencyconverter.mainactivity.MainActivityPresenter
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule(private val activity: MainActivity) {

    @MainActivityScope
    @Provides
    fun provideMainActivityPresenter(repository: CurrencyRateRepository): MainActivityPresenter {
        return MainActivityPresenterImpl(activity, repository)
    }
}