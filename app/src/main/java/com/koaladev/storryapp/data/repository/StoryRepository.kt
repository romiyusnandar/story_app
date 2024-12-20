package com.koaladev.storryapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.koaladev.storryapp.data.local.StoryPagingSource
import com.koaladev.storryapp.data.response.AddStoryResponse
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.retrofit.ApiServices
import kotlinx.coroutines.flow.Flow
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

    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).flow
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