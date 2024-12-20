package com.koaladev.storryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.repository.StoryRepository
import com.koaladev.storryapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {
    private val _userSession = MutableLiveData<UserModel>()
    val userSession: LiveData<UserModel> = _userSession

    init {
        getSession()
    }

    private fun getSession() {
        viewModelScope.launch {
            repository.getSession().collect { userModel ->
                _userSession.value = userModel
            }
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getStories(token).cachedIn(viewModelScope)
    }

}