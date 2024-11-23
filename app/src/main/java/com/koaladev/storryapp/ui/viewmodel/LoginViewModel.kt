package com.koaladev.storryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "login: $email, $password")
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.error == true) {
                    onResult(false)
                } else {
                    onResult(true)
                    Log.d(TAG, "login: $email, $password")
                }
            } catch (e: Exception) {
                onResult(false)
                Log.e(TAG, "login: ${e.message}")
            }
        }
    }
}