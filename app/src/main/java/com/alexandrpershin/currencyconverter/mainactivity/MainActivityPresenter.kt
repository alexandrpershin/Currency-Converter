package com.alexandrpershin.currencyconverter.mainactivity

interface MainActivityPresenter {

    fun start()

    fun pause()

    fun stop()

    fun updateBaseCurrency(currency: String)

}