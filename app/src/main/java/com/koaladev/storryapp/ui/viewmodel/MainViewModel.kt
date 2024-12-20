package com.koaladev.storryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.repository.StoryRepository
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.response.StoryResponse
import com.koaladev.storryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private val _stories = MutableLiveData<PagingData<ListStoryItem>>()
    val stories: MutableLiveData<PagingData<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            storyRepository.getStories(token)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _stories.postValue(pagingData)
                    _isLoading.value = false
                }
        }
    }
}