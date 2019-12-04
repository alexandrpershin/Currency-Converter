package com.alexandrpershin.currencyconverter.util

import android.view.View


fun Double.round(decimal: Int): Double {
    return "%.${decimal}f".format(this).toDouble()
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

