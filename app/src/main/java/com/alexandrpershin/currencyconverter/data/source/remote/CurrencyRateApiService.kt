package com.alexandrpershin.currencyconverter.data.source.remote

import android.content.Context
import com.alexandrpershin.currencyconverter.BuildConfig
import com.alexandrpershin.currencyconverter.model.CurrencyRateResponse
import com.alexandrpershin.currencyconverter.util.Constants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface CurrencyRateApiService {

    companion object {
        private val gson: Gson = GsonBuilder().create()

        private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

        private fun provideRetrofit(context: Context): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(gsonFactory)
                .client(provideOkHttpClient(context))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()
        }

        private fun provideOkHttpClient(context: Context): OkHttpClient {

            val builder: OkHttpClient.Builder = OkHttpClient.Builder()

            // print in console all request/response
            val consoleInterceptor = HttpLoggingInterceptor().also {
                it.level = when (BuildConfig.DEBUG) {
                    true -> HttpLoggingInterceptor.Level.BODY
                    else -> HttpLoggingInterceptor.Level.NONE
                }
            }

            builder.connectTimeout(5, TimeUnit.SECONDS)
            builder.readTimeout(5, TimeUnit.SECONDS)
            builder.addInterceptor(consoleInterceptor)

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(ChuckInterceptor(context)) //Very cool interceptor library
            }

            return builder.build()
        }

        fun create(context: Context): CurrencyRateApiService {
            val retrofit = provideRetrofit(context)
            return retrofit.create(CurrencyRateApiService::class.java)
        }
    }

    @GET("/latest")
    fun getCurrencyRate(@Query("base") baseCurrency: String): Single<Response<CurrencyRateResponse>>

}