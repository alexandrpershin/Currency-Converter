package com.alexandrpershin.currencyconverter.mainactivity

import com.alexandrpershin.currencyconverter.data.Result
import com.alexandrpershin.currencyconverter.data.source.remote.CurrencyRateApiService
import com.alexandrpershin.currencyconverter.model.CurrencyRateResponse
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import com.alexandrpershin.currencyconverter.util.CurrencyRateMapper
import com.alexandrpershin.currencyconverter.util.currenciesNameMap
import com.alexandrpershin.currencyconverter.util.round
import io.reactivex.Single
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.lang.Thread.sleep
import kotlin.random.Random


class MainActivityPresenterTest {

    @Mock
    lateinit var mockService: CurrencyRateApiService

    @Mock
    lateinit var errorResponseBody: ResponseBody

    @Mock
    lateinit var view: MainActivityView

    lateinit var repository: CurrencyRateRepository

    lateinit var presenter: MainActivityPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = CurrencyRateRepository((mockService))

        presenter = MainActivityPresenterFake(view, repository)
    }

    @Test
    fun updateBaseCurrency() {
        val newCurrency = "RUB"
        presenter.updateBaseCurrency(newCurrency)

        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val currencyRateResponse =
            CurrencyRateResponse(newCurrency, "20-11-2011", rates as HashMap<String, Double>)
        val retrofitResponse = Response.success(currencyRateResponse)

        `when`(mockService.getCurrencyRate(newCurrency))
            .thenReturn(Single.just(retrofitResponse))

        val testObserver = repository.loadCurrencyRate(newCurrency).test()

        testObserver.assertNoErrors()

        testObserver.assertValue {
            return@assertValue if (it is Result.Success) {
                it.data.find { it.code == newCurrency }?.rate == 1.0
            } else {
                false
            }
        }
    }

    @Test
    fun pause() {
        val currency = "EUR"

        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val currencyRateResponse =
            CurrencyRateResponse(currency, "20-11-2011", rates as HashMap<String, Double>)
        val retrofitResponse = Response.success(currencyRateResponse)

        val result = CurrencyRateMapper.fromResponse(currencyRateResponse)

        `when`(mockService.getCurrencyRate(currency))
            .thenReturn(Single.just(retrofitResponse))


        `when`(mockService.getCurrencyRate(currency))
            .thenReturn(Single.just(retrofitResponse))

        presenter.pause()
        sleep(1000)
        verify(view, times(0)).showNoDataError()
        verify(view, times(0)).showNoInternetError()
        verify(view, times(0)).hideErrorView()
        verify(view, times(0)).updateData(result)
    }

    @Test
    fun start_success() {
        val currency = "EUR"

        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val currencyRateResponse =
            CurrencyRateResponse(currency, "20-11-2011", rates as HashMap<String, Double>)
        val retrofitResponse = Response.success(currencyRateResponse)

        val result = CurrencyRateMapper.fromResponse(currencyRateResponse)

        `when`(mockService.getCurrencyRate(currency))
            .thenReturn(Single.just(retrofitResponse))

        presenter.start()

        sleep(2000)
        verify(view, times(2)).updateData(result)
    }

    @Test
    fun start_and_show_error() {
        val currency = "EUR"

        val errorResponse = Response.error<CurrencyRateResponse>(500, errorResponseBody)

        `when`(mockService.getCurrencyRate(currency)).thenReturn(Single.just(errorResponse))

        presenter.start()

        sleep(1000)
        verify(view).showErrorToast(errorResponse.message())
    }

    @Test
    fun start_and_show_no_data_error() {
        val currency = "EUR"

        val rates = hashMapOf<String, Double>()
        val currencyRateResponse =
            CurrencyRateResponse(currency, "20-11-2011", rates)
        val retrofitResponse = Response.success(currencyRateResponse)

        `when`(mockService.getCurrencyRate(currency)).thenReturn(Single.just(retrofitResponse))

        presenter.start()

        sleep(1000)
        verify(view).showNoDataError()
    }

    @Test
    fun stop() {
        val currency = "EUR"

        val rates =
            currenciesNameMap.keys.map { it to Random.nextDouble(0.01, 100.0).round(2) }.toMap()
        val currencyRateResponse =
            CurrencyRateResponse(currency, "20-11-2011", rates as HashMap<String, Double>)
        val retrofitResponse = Response.success(currencyRateResponse)

        val result = CurrencyRateMapper.fromResponse(currencyRateResponse)

        `when`(mockService.getCurrencyRate(currency))
            .thenReturn(Single.just(retrofitResponse))


        `when`(mockService.getCurrencyRate(currency))
            .thenReturn(Single.just(retrofitResponse))

        presenter.stop()
        sleep(1000)
        verify(view, times(0)).showNoDataError()
        verify(view, times(0)).showNoInternetError()
        verify(view, times(0)).hideErrorView()
        verify(view, times(0)).updateData(result)
    }
}