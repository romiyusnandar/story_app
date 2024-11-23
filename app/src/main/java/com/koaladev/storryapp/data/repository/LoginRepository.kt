package com.koaladev.storryapp.data.repository

import com.koaladev.storryapp.data.response.LoginResponse
import com.koaladev.storryapp.data.retrofit.ApiServices

class LoginRepository private constructor(
    private val apiServices: ApiServices
) {
    suspend fun login(email: String, password: String): LoginResponse {
        return apiServices.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiServices: ApiServices
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiServices)
            }.also { instance = it }
    }
}