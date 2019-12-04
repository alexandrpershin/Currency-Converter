package com.alexandrpershin.currencyconverter.repository

import com.alexandrpershin.currencyconverter.data.Result
import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.model.Currency
import com.alexandrpershin.currencyconverter.util.CurrencyRateMapper
import io.reactivex.Single
import java.net.UnknownHostException

class CurrencyRateRepository(
    private val currencyRateApiService: CurrencyRateApiService
) {

    fun loadCurrencyRate(baseCurrency: String): Single<Result<ArrayList<Currency>>> {
        return currencyRateApiService
            .getCurrencyRate(baseCurrency)
            .map { result ->
                if (result.isSuccessful) {
                    val parsedResponse = CurrencyRateMapper.fromResponse(result.body())

                    if (parsedResponse.isNotEmpty()) {
                        Result.Success(parsedResponse)
                    } else {
                        Result.EmptyData
                    }

                } else {
                    Result.Error(result.message())
                }
            }
            .onErrorReturn {
                if (it is UnknownHostException) {
                    Result.NoInternet
                } else {
                    Result.Error(it.message ?: "Unexpected error occurred")
                }
            }
    }

}