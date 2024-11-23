package com.koaladev.storryapp.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyEditTextPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var isPassword = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (isPassword) {
                    validatePassword(s.toString())
                }
            }
        })
    }

    fun setAsPassword() {
        isPassword = true
    }

    private fun validatePassword(password: String) {
        error = if (password.length < 8) {
            "Password harus memiliki minimal 8 karakter"
        } else {
            null
        }
    }
}