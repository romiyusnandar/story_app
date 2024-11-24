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

    fun login(
        email: String, password: String, onResult: (Boolean, String, String, String) -> Unit) {
        Log.d(TAG, "Melakukan login untuk: $email")
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (!response.error!!) {
                    val loginResult = response.loginResult
                    if (loginResult != null) {
                        Log.d(TAG, "Login successful for email: $email")
                        onResult(
                            true,
                            loginResult.userId.toString(),
                            loginResult.name.toString(),
                            loginResult.token.toString()
                        )
                    } else {
                        Log.e(TAG, "Login failed: Login result is null")
                        onResult(false, "", "", "")
                    }
                } else {
                    Log.e(TAG, "Login failed: ${response.message}")
                    onResult(false, "", "", "")
                }
            } catch (e: Exception) {
                onResult(false, "", "", "")
                Log.e(TAG, "login: ${e.message}")
            }
        }
    }
}