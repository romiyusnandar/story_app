package com.koaladev.storryapp.data.response

data class StoryResponse(
    val error: Boolean? = null,
    val message: String? = null,
    val listStory: List<ListStoryItem?>? = null
)

data class ListStoryItem(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val lon: Any? = null,
    val lat: Any? = null
)