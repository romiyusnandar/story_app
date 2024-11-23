package com.koaladev.storryapp.data.repository

import com.koaladev.storryapp.data.response.SignupResponse
import com.koaladev.storryapp.data.retrofit.ApiServices

class SignupRepository private constructor(
    private val apiServices: ApiServices
){
    suspend fun register(name: String, email: String, password: String): SignupResponse {
        return apiServices.signup(name, email, password)
    }

    companion object {
        @Volatile
        private var instance: SignupRepository? = null
        fun getInstance(
            apiServices: ApiServices
        ): SignupRepository =
            instance ?: synchronized(this) {
                instance ?: SignupRepository(apiServices)
            }.also { instance = it }
    }
}