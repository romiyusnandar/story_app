package com.koaladev.storryapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.retrofit.ApiServices

class StoryPagingSource(private val apiService: ApiServices, private val token: String) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer $token", page, params.loadSize)

            if (responseData.error === "false") {
                val stories = responseData.listStory
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(responseData.message))
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}