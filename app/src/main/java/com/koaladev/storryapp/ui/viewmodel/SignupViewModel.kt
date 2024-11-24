package com.koaladev.storryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.storryapp.data.repository.SignupRepository
import kotlinx.coroutines.launch

class SignupViewModel (private val repository: SignupRepository) : ViewModel() {
    companion object {
        const val TAG = "SignupViewModel"
    }

    fun signup(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                if (response.error == true) {
                    onResult(false, response.message ?: "Unknown error occurred")
                    Log.d(TAG, "Signup failed: ${response.message}")
                } else {
                    onResult(true, "Signup successful")
                    Log.d(TAG, "Signup successful: ${response.message}")
                }
            } catch (e: Exception) {

                onResult(false, e.message.toString())
            }
        }
    }
}