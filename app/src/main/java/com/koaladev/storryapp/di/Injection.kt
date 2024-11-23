package com.koaladev.storryapp.di

import android.content.Context
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.repository.SignupRepository
import com.koaladev.storryapp.data.pref.UserPreference
import com.koaladev.storryapp.data.pref.dataStore
import com.koaladev.storryapp.data.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref, ApiConfig.getApiService())
    }

    fun provideSignupRepository(context: Context): SignupRepository {
        val apiService = ApiConfig.getApiService()
        return SignupRepository.getInstance(apiService)
    }
}