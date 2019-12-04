package com.alexandrpershin.currencyconverter.model

data class Change<out T>(
    val oldData: T,
    val newData: T
)