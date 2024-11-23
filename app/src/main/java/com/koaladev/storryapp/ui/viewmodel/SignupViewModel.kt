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

    fun signup(username: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.register(username, email, password)
                if (response.error == true) {
                    onResult(false)
                } else {
                    onResult(true)
                    Log.d(TAG, "registerUser: ${response.message}")
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}