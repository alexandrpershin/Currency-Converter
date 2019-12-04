package com.alexandrpershin.currencyconverter.model

data class CurrencyRateResponse(
    val base: String,
    val date: String,
    val rates: HashMap<String,Double>?
)




