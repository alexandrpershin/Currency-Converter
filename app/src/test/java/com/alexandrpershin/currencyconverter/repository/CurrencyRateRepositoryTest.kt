package com.alexandrpershin.currencyconverter.repository

import com.alexandrpershin.currencyconverter.data.Result
import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.model.CurrencyRateResponse
import com.alexandrpershin.currencyconverter.util.currenciesNameMap
import com.alexandrpershin.currencyconverter.util.round
import io.reactivex.Single
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import kotlin.random.Random


class CurrencyRateRepositoryTest {

    @Mock
    lateinit var mockService: CurrencyRateApiService

    @Mock
    lateinit var errorResponseBody: ResponseBody

    lateinit var repository: CurrencyRateRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = CurrencyRateRepository((mockService))
    }

    @Test
    fun test_load_currency_rate_success() {
        val baseCurrency = "EUR"

        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val currencyRateResponse =
            CurrencyRateResponse(baseCurrency, "20-11-2011", rates as HashMap<String, Double>)
        val response = Response.success(currencyRateResponse)

        Mockito.`when`(mockService.getCurrencyRate(baseCurrency)).thenReturn(Single.just(response))

        val testObserver = repository.loadCurrencyRate(baseCurrency).test()

        testObserver.assertNoErrors()

        testObserver.assertValue {
            return@assertValue if (it is Result.Success) {
                it.data.size == (rates.size + 1)
            } else {
                false
            }
        }
    }

    @Test
    fun test_load_currency_rate_empty() {
        val baseCurrency = "EUR"

        val rates = hashMapOf<String, Double>()
        val currencyRateResponse = CurrencyRateResponse(baseCurrency, "20-11-2011", rates)
        val response = Response.success(currencyRateResponse)

        Mockito.`when`(mockService.getCurrencyRate(baseCurrency)).thenReturn(Single.just(response))

        val testObserver = repository.loadCurrencyRate(baseCurrency).test()

        testObserver.assertNoErrors()

        testObserver.assertValue {
            return@assertValue it is Result.EmptyData
        }
    }

    @Test
    fun test_load_currency_rate_error() {
        val baseCurrency = "EUR"

        val errorResponse = Response.error<CurrencyRateResponse>(500, errorResponseBody)

        Mockito.`when`(mockService.getCurrencyRate(baseCurrency)).thenReturn(Single.just(errorResponse))

        val testObserver = repository.loadCurrencyRate(baseCurrency).test()

        testObserver.assertNoErrors()

        testObserver.assertValue {
            return@assertValue it is Result.Error
        }
    }



}