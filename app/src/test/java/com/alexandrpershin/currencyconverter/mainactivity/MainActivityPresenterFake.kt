package com.alexandrpershin.currencyconverter.mainactivity

import com.alexandrpershin.currencyconverter.data.Result
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivityPresenterFake(
    private val view: MainActivityView,
    private val repository: CurrencyRateRepository
) : MainActivityPresenter {

    private var baseCurrency: String = DEFAULT_CURRENCY

    private var disposable: CompositeDisposable? = CompositeDisposable()

    private var isAlive = false

    private fun initLoadingData() {
        thread {
            while (isAlive) {
                try {
                    loadCurrencyRate(baseCurrency)
                    TimeUnit.SECONDS.sleep(1)
                } catch (ex: Exception) {
                    view.showErrorToast(ex.message ?: "Unexpected error")
                }
            }
        }
    }

    private fun loadCurrencyRate(currency: String) {
        val request = repository.loadCurrencyRate(currency)
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                when (result) {
                    is Result.Success -> {
                        view.hideErrorView()
                        view.updateData(result.data)
                    }
                    is Result.EmptyData -> {
                        view.showNoDataError()
                    }
                    is Result.NoInternet -> {
                        view.showNoInternetError()
                    }

                    is Result.Error -> {
                        view.showErrorToast(result.message)
                    }
                }
            },
                { error ->
                    view.showErrorToast(error.message.toString())

                })

        disposable?.add(request)
    }

    override fun updateBaseCurrency(newCurrency: String) {
        baseCurrency = newCurrency
    }

    override fun pause() {
        isAlive = false
    }

    override fun start() {
        isAlive = true
        initLoadingData()
    }

    override fun stop() {
        releaseResources()
    }

    private fun releaseResources() {
        isAlive = false
        if (disposable != null) {
            disposable!!.clear()
            disposable = null
        }
    }

    companion object {
        const val DEFAULT_CURRENCY = "EUR"
    }

}

