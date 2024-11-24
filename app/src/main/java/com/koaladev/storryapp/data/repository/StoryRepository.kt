package com.koaladev.storryapp.data.repository

import com.koaladev.storryapp.data.response.AddStoryResponse
import com.koaladev.storryapp.data.retrofit.ApiServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiServices) {
    suspend fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): AddStoryResponse {
        return apiService.addStory("Bearer $token", photo, description, lat, lon)
    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiServices): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}