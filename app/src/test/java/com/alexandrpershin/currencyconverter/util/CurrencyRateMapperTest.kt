package com.alexandrpershin.currencyconverter.util

import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.model.CurrencyRateResponse
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import okhttp3.ResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mock
import kotlin.random.Random

class CurrencyRateMapperTest {

    @Mock
    lateinit var mockService: CurrencyRateApiService

    @Mock
    lateinit var errorResponseBody: ResponseBody

    lateinit var repository: CurrencyRateRepository


    @Test
    fun test_empty_response() {
        val rates = hashMapOf<String, Double>()
        val response = CurrencyRateResponse("", "", rates)
        val result = CurrencyRateMapper.fromResponse(response)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test_not_empty_response() {
        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val response = CurrencyRateResponse("", "", rates as HashMap<String, Double>)
        val result = CurrencyRateMapper.fromResponse(response)

        assertTrue(result.isNotEmpty())
    }
}