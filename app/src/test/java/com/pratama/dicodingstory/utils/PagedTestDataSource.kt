package com.pratama.dicodingstory.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pratama.dicodingstory.data.local.entity.Story

class PagedTestDataSource :
    PagingSource<Int, Story>() {

    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}