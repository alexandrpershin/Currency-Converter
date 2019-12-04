package com.alexandrpershin.currencyconverter.mainactivity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexandrpershin.currencyconverter.R
import com.alexandrpershin.currencyconverter.di.Injector
import com.alexandrpershin.currencyconverter.model.Currency
import com.alexandrpershin.currencyconverter.mainactivity.adapter.CurrencyRateAdapter
import com.alexandrpershin.currencyconverter.util.makeGone
import com.alexandrpershin.currencyconverter.util.makeVisible
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView {

    @Inject
    lateinit var presenter: MainActivityPresenter

    private lateinit var adapter: CurrencyRateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Injector.initialiseMainActivityComponent(this)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = CurrencyRateAdapter()
        adapter.interaction = { currencyCode ->
            presenter.updateBaseCurrency(currencyCode)
        }
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun updateData(newData: ArrayList<Currency>) {
        recyclerView.makeVisible()
        adapter.updateData(newData)
    }

    override fun hideErrorView() {
        errorViewTv.makeGone()
    }

    override fun showNoDataError() {
        recyclerView.makeGone()
        errorViewTv.makeVisible()
        errorViewTv.text = getString(R.string.error_message_no_data)
    }

    override fun showNoInternetError() {
        recyclerView.makeGone()
        errorViewTv.makeVisible()
        errorViewTv.text = getString(R.string.error_message_no_internet)
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
        Injector.releaseMainActivityComponent()

    }
}
