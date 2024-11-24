package com.koaladev.storryapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.repository.StoryRepository
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel (private val storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _uploadStatus = MutableLiveData<Result<AddStoryResponse>>()
    val uploadStatus: LiveData<Result<AddStoryResponse>> = _uploadStatus

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadNewStory(token, photo, description, lat, null)
                _uploadStatus.value = Result.success(response)
                _isLoading.value = false
            } catch (e: Exception) {
                _uploadStatus.value = Result.failure(e)
                _isLoading.value = false
            }
        }
    }
}