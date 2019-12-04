package com.alexandrpershin.currencyconverter.util

import com.alexandrpershin.currencyconverter.R
import com.alexandrpershin.currencyconverter.model.Currency
import com.alexandrpershin.currencyconverter.model.CurrencyRateResponse


object CurrencyRateMapper {

    fun fromResponse(response: CurrencyRateResponse?): ArrayList<Currency> {
        val currencyRateList = ArrayList<Currency>()

        if (response?.rates != null && response.rates.isNotEmpty()) {
            val baseCurrencyRate = createCurrencyRate(response.base, 1.0)

            currencyRateList.add(baseCurrencyRate)

            response.rates.forEach { (code, rate) ->
                currencyRateList.add(createCurrencyRate(code, rate))
            }
        }

        return currencyRateList
    }

    private fun createCurrencyRate(code: String, rate: Double): Currency {
        return Currency(
            code,
            currenciesNameMap[code] ?: code,
            rate,
            currenciesIconMap[code] ?: R.raw.ic_default_currency
        )
    }

}
