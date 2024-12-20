package com.koaladev.storryapp.data.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("listStory")
    val listStory: List<ListStoryItem>
)

@Parcelize
@Entity(tableName = "story")
data class ListStoryItem(
    @PrimaryKey @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lon")
    val lon: Double?
) : Parcelable