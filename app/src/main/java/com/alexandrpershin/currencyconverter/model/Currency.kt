package com.alexandrpershin.currencyconverter.model

data class Currency(
    var code: String,
    var name: String,
    var rate: Double,
    var drawableRes: Int,
    var value: Double = rate
)