package com.koaladev.storryapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.koaladev.storryapp.data.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)
    @Query("SELECT * FROM listStoryItem")
    fun getAllStory(): PagingSource<Int, ListStoryItem>
}