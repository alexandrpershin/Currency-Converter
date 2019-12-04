package com.alexandrpershin.currencyconverter.data

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    object EmptyData : Result<Nothing>()
    object NoInternet : Result<Nothing>()
    data class Error(val message: String) : Result<Nothing>()
}