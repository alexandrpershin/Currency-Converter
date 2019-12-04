package com.alexandrpershin.currencyconverter.`interface`

import android.text.Editable
import android.text.TextWatcher

abstract class TextChangeListener : TextWatcher {

    private var initialized = false  // it is needed for prevent unnecessary firs call afterTextChanged when textWatcher is added to EditText

    override fun afterTextChanged(text: Editable?) {
        if (initialized) {
            afterTextChanged(text.toString())
        }

        if (!initialized) {
            initialized = true
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    abstract fun afterTextChanged(text: String)
}