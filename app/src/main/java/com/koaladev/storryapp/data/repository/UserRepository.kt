package com.koaladev.storryapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.pref.UserPreference
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.response.LoginResponse
import com.koaladev.storryapp.data.retrofit.ApiServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiServices
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getPage(token: String): Flow<PagingData<ListStoryItem>> {
        return if (token.isNotEmpty()) {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    StoryPagingSource(apiService, "Bearer $token")
                }
            ).flow
        } else {
            flowOf(PagingData.empty())
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiServices
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}