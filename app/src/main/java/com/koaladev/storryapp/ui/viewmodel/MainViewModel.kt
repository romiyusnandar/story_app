package com.koaladev.storryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.response.StoryResponse
import com.koaladev.storryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private val _stories = MutableLiveData<List<ListStoryItem?>>()
    val stories: MutableLiveData<List<ListStoryItem?>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(location: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getSession().collect{ user ->
                val token = user.token
                val locationParam = if (location) "1" else "0"

                ApiConfig.getApiService().getAllStories("Bearer $token", locationParam).enqueue(
                    object : Callback<StoryResponse> {
                        override fun onResponse(
                            call: Call<StoryResponse>,
                            response: Response<StoryResponse>
                        ) {
                            if (response.isSuccessful) {
                                _isLoading.value = false
                                response.body().let { storyResponse ->
                                    _stories.value = storyResponse?.listStory
                                }
                            } else {
                                _isLoading.value = false
                                Log.e("MainViewModel", "onFailure: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                            _isLoading.value = false
                            Log.e("MainViewModel", "onFailure: ${t.message.toString()}")
                        }
                    }
                )
            }
        }
    }
}