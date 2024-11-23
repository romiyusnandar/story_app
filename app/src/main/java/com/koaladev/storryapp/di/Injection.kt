package com.koaladev.storryapp.di

import android.content.Context
import com.koaladev.storryapp.data.UserRepository
import com.koaladev.storryapp.data.pref.UserPreference
import com.koaladev.storryapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}