package com.koaladev.storryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.koaladev.storryapp.data.local.StoryDatabase
import com.koaladev.storryapp.data.local.StoryRemoteMediator
import com.koaladev.storryapp.data.response.AddStoryResponse
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.retrofit.ApiServices
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiServices,
    private val storyDatabase: StoryDatabase
) {
    suspend fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): AddStoryResponse {
        return apiService.addStory("Bearer $token", photo, description, lat, lon)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).flow
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiServices,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase)
            }.also { instance = it }
    }
}