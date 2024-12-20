package com.koaladev.storryapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.retrofit.ApiServices
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
class StoryPagingSource(
    private val apiService: ApiServices,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 10
        const val LOAD_DELAY = 3000L
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return try {
            delay(LOAD_DELAY) // Add delay between requests
            val response = apiService.getStories("Bearer $token", position, NETWORK_PAGE_SIZE)
            val stories = response.listStory
            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (stories.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}