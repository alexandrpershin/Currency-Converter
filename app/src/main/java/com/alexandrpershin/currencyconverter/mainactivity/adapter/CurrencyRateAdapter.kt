package com.alexandrpershin.currencyconverter.mainactivity.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexandrpershin.currencyconverter.R
import com.alexandrpershin.currencyconverter.`interface`.TextChangeListener
import com.alexandrpershin.currencyconverter.mainactivity.MainActivityPresenterImpl
import com.alexandrpershin.currencyconverter.model.Currency
import com.alexandrpershin.currencyconverter.util.round
import com.bumptech.glide.Glide
import java.util.concurrent.atomic.AtomicBoolean

class CurrencyRateAdapter : RecyclerView.Adapter<CurrencyRateAdapter.CurrencyRateViewHolder>() {

    private var baseRate: String = MainActivityPresenterImpl.DEFAULT_CURRENCY

    private var baseValue: Double = 1.0

    private var items = arrayListOf<Currency>()

    private val lock = AtomicBoolean(false)  // used to prevent simultaneously modification of items

    var interaction: ((String) -> Unit)? = null

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
        return CurrencyRateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency_rate, parent, false),
            interaction
        )
    }

    override fun onBindViewHolder(holder: CurrencyRateViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateData(newData: ArrayList<Currency>) {
        if (!lock.get()) {  // if items aren't in modification process then update items else do nothing
            lock.set(true)

            if (this.items.isNotEmpty()) {   // update all data beside first base currency
                updateAllBesidesBase(newData)
            } else {  // set new data for first time
                notifyDataSetChanged(newData)
            }
            lock.set(false)
        }
    }

    private fun updateAllBesidesBase(newRatesFromServer: ArrayList<Currency>) {
        val sortedList = arrayListOf<Currency>()

        this.items.map { it.code }.forEachIndexed { index, code ->
            val element = newRatesFromServer.find { it.code == code }
            element?.let {
                if (index > 0) {  // modify items that go after first item
                    element.value = element.rate * baseValue
                }
                sortedList.add(element)
            }
        }

        notifyDataSetChanged(sortedList)
    }

    private fun notifyDataSetChanged(newItems: ArrayList<Currency>) {
        val result = DiffUtil.calculateDiff(CurrencyRateDiffUtilCallback(this.items, newItems))
        result.dispatchUpdatesTo(this)
        this.items.clear()
        this.items.addAll(newItems)
    }

    private val textChangeListener = object : TextChangeListener() {
        override fun afterTextChanged(text: String) {
            baseValue = try {
                text.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
            onBaseValueChanged()
        }
    }

    /*  Called when value(not rate) of baseCurrency was changed in order to
    *   recalculate value of rest of currencies
    */

    private fun onBaseValueChanged() {
        if (!lock.get()) {
            lock.set(true)

            val updatedItems = ArrayList<Currency>()

            items.forEachIndexed { index, currencyRate ->
                val item = currencyRate.copy()
                if (index > 0) {
                    item.value = item.rate * baseValue
                }
                updatedItems.add(item)
            }

            Handler().postDelayed({
                notifyDataSetChanged(updatedItems)
                lock.set(false)
            }, 100)
        }
    }

    private fun moveItem(fromPosition: Int, toPosition: Int) {

        val tempList = ArrayList<Currency>(items)

        if (fromPosition == toPosition) return

        val movingItem = tempList.removeAt(fromPosition)

        if (fromPosition < toPosition) {
            tempList.add(toPosition - 1, movingItem)
        } else {
            tempList.add(toPosition, movingItem)
        }

        notifyDataSetChanged(tempList)
    }

    inner class CurrencyRateViewHolder(
        itemView: View,
        private val interaction: ((String) -> Unit)?
    ) :
        RecyclerView.ViewHolder(itemView) {

        private val currencyFlagIv = itemView.findViewById<ImageView>(R.id.currencyFlagIv)
        private val currencyCodeTv = itemView.findViewById<TextView>(R.id.currencyCodeTv)
        private val currencyNameTv = itemView.findViewById<TextView>(R.id.currencyNameTv)
        private val currencyValueEt = itemView.findViewById<EditText>(R.id.currencyValueEt)

        private var itemClicked: Boolean = false

        fun bind(position: Int) = with(itemView) {
            val currencyRate = items[position]

            Glide.with(context).load(currencyRate.drawableRes).into(currencyFlagIv)

            currencyCodeTv.text = currencyRate.code
            currencyNameTv.text = currencyRate.name

            if (currencyRate.code != baseRate && currencyValueEt.tag != null) {
                currencyValueEt.tag = null
                currencyValueEt.removeTextChangedListener(textChangeListener)
            }

            if (currencyRate.code == baseRate && currencyValueEt.tag == null) {
                currencyValueEt.tag = baseRate
                currencyValueEt.addTextChangedListener(textChangeListener)
            }

            if (position == 0) {  // items[0] is base currency, just set baseValue otherwise adapter will recreate viewHolder
                // and [currencyValueEt] will lose it's focus
                currencyValueEt.setText((baseValue.round(2)).toString())
            } else {
                currencyValueEt.setText((currencyRate.value.round(2)).toString())
            }

            itemView.setOnClickListener {
                itemClicked = true
                moveItemToTop(currencyRate)
            }

            currencyValueEt.setOnFocusChangeListener { view, focused ->
                if (focused && !itemClicked) { // prevent call OnFocusChange() when click event was triggered
                    moveItemToTop(currencyRate)
                }
            }
        }

        private fun moveItemToTop(currencyRate: Currency) {

            val action = {
                if (baseRate != currencyRate.code) {
                    lock.set(true)

                    moveItem(adapterPosition, 0)
                    interaction?.invoke(currencyRate.code)
                    currencyValueEt.requestFocus()

                    items[1].value = baseValue  // update previous base currency

                    baseRate = currencyRate.code
                    baseValue = currencyRate.value

                    itemClicked = false

                    lock.set(false)
                }
            }

            if (!lock.get()) {  // if items are not in process of modification then move item immediately
                action.invoke()
            } else { // if items are in process of modification then move item after 100 millis
                Handler().postDelayed({
                    action.invoke()
                }, 100)
            }
        }
    }
}


