package com.alexandrpershin.currencyconverter.mainactivity

import com.alexandrpershin.currencyconverter.model.Currency

interface MainActivityView {

    fun showErrorToast(message : String)

    fun showNoDataError()

    fun showNoInternetError()

    fun hideErrorView()

    fun updateData(newData : ArrayList<Currency>)
}