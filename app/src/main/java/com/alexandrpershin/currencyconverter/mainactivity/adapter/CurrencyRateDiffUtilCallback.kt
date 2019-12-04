package com.alexandrpershin.currencyconverter.mainactivity.adapter

import androidx.recyclerview.widget.DiffUtil
import com.alexandrpershin.currencyconverter.model.Change
import com.alexandrpershin.currencyconverter.model.Currency

class CurrencyRateDiffUtilCallback(
    private var oldItems: List<Currency>,
    private var newItems: List<Currency>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].code == newItems[newItemPosition].code

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].rate == newItems[newItemPosition].rate
                && oldItems[oldItemPosition].value == newItems[newItemPosition].value

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return Change(oldItem, newItem)
    }
}