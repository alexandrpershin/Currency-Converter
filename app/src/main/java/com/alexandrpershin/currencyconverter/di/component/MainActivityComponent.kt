package com.alexandrpershin.currencyconverter.di.component

import com.alexandrpershin.currencyconverter.di.module.MainActivityModule
import com.alexandrpershin.currencyconverter.di.scope.MainActivityScope
import com.alexandrpershin.currencyconverter.mainactivity.MainActivity
import dagger.Component

@MainActivityScope
@Component(dependencies = [AppComponent::class], modules = [MainActivityModule::class])
interface MainActivityComponent {
    fun inject(activity: MainActivity)
}