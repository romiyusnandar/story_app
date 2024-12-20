package com.koaladev.storryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.response.StoryResponse
import com.koaladev.storryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Callback

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private val _stories = MutableLiveData<List<ListStoryItem?>>()
    val stories: MutableLiveData<List<ListStoryItem?>> = _stories

    fun getStoriesNearby(location: Boolean) {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                val token = user.token
                val locationParam = if (location) "1" else "0"

                ApiConfig.getApiService().getAllStories("Bearer $token", locationParam).enqueue(
                    object : Callback<StoryResponse> {
                        override fun onResponse(
                            call: retrofit2.Call<StoryResponse>,
                            response: retrofit2.Response<StoryResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body().let { stories ->
                                    _stories.value = stories?.listStory
                                }
                            } else {
                                Log.d("MapsViewModel", "onFailure: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<StoryResponse>, t: Throwable) {
                            Log.d("MapsViewModel", "onFailure: ${t.message.toString()}")
                        }
                    }
                )
            }
        }
    }
}