package com.alexandrpershin.currencyconverter.mainactivity

import android.util.Log
import com.alexandrpershin.currencyconverter.data.Result
import com.alexandrpershin.currencyconverter.repository.CurrencyRateRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivityPresenterImpl(
    private val view: MainActivityView,
    private val repository: CurrencyRateRepository
) : MainActivityPresenter {

    private var baseCurrency: String =
        DEFAULT_CURRENCY

    private var disposable: CompositeDisposable? = CompositeDisposable()

    private var isAlive = false

    private fun initLoadingData() {
        thread {
            while (isAlive) {
                try {
                    Log.i(TAG, "initLoadingData")
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
            .observeOn(AndroidSchedulers.mainThread())
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
        private val TAG = MainActivityPresenterImpl::class.java.simpleName
        const val DEFAULT_CURRENCY = "EUR"
    }

}