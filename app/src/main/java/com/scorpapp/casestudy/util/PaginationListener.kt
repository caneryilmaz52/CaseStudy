package com.scorpapp.casestudy.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    abstract fun getMoreData()

    abstract fun isBusy(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val childCount: Int = layoutManager.childCount
        val itemCount: Int = layoutManager.itemCount
        val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

        val canGetMoreData = (childCount + firstVisibleItemPosition) >= itemCount && firstVisibleItemPosition >= 0

        if (canGetMoreData && !isBusy()) {
            getMoreData()
        }
    }
}