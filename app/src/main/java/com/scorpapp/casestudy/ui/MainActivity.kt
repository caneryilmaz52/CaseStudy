package com.scorpapp.casestudy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.scorpapp.casestudy.R
import com.scorpapp.casestudy.adapter.PersonRecyclerViewAdapter
import com.scorpapp.casestudy.data.DataSource
import com.scorpapp.casestudy.data.Person
import com.scorpapp.casestudy.extensions.*
import com.scorpapp.casestudy.util.LocalDataStore
import com.scorpapp.casestudy.util.PaginationListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var localDataStore: LocalDataStore
    private lateinit var dataSource: DataSource

    private lateinit var swipeRefreshRoot: SwipeRefreshLayout
    private lateinit var personRecyclerView: RecyclerView
    private lateinit var errorTextView: AppCompatTextView

    private lateinit var personRecyclerViewAdapter: PersonRecyclerViewAdapter

    private var isInformationShowed: Boolean = false

    private var nextPage: String? = null
    private var reachedLastPage: Boolean = false

    private val retryingLimit: Int = 3
    private var retryingAttempt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initData()
    }

    private fun bindViews() {
        swipeRefreshRoot = swipeRefreshLayout
        personRecyclerView = rvPerson
        errorTextView = tvError

        setupRecyclerView()
        setupSwipeRefresh()
    }

    private fun initData() {
        localDataStore = LocalDataStore(this)
        isInformationShowed = localDataStore.getData("isInformationShowed")

        dataSource = DataSource()
        getInitializeData()
    }

    private fun setupRecyclerView() {
        personRecyclerViewAdapter = PersonRecyclerViewAdapter()
        personRecyclerView.adapter = personRecyclerViewAdapter

        val layoutManager = personRecyclerView.layoutManager as LinearLayoutManager

        personRecyclerView.setOnScrollListener(object : PaginationListener(layoutManager) {
            override fun getMoreData() {

                if (nextPage == null && !reachedLastPage) {
                    reachedLastPage = true
                    showCommonPopup(getString(R.string.all_people_listed))
                } else if (nextPage != null && !reachedLastPage) {
                    getNextPageData()
                }
            }

            override fun isBusy(): Boolean {
                return isLoading()
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefreshRoot.setOnRefreshListener {
            swipeRefreshRoot.isRefreshing = false

            clearPersonRecyclerView()
            getInitializeData()
        }
    }

    private fun clearPersonRecyclerView() {
        personRecyclerViewAdapter.differ.submitList(emptyList())
    }

    private fun getInitializeData() {
        showLoading()
        dataSource.fetch(null) { fetchResponse, fetchError ->
            hideLoading()

            fetchError?.let { error ->
                showRetryingPopup(error.errorDescription)
            }

            fetchResponse?.let { response ->
                if (response.people.isEmpty()) {
                    nextPage = null
                    showRetryingPopup(getString(R.string.no_record))
                } else {
                    retryingAttempt = 0

                    nextPage = response.next
                    reachedLastPage = false

                    personRecyclerViewAdapter.differ.submitList(response.people)

                    if (!isInformationShowed) showInformationMessage()
                }
            }
        }
    }

    private fun showInformationMessage() {
        isInformationShowed = true

        localDataStore.saveData("isInformationShowed", isInformationShowed)

        showCommonPopup(getString(R.string.app_usage_info))
    }

    private fun getNextPageData() {
        showLoading()
        dataSource.fetch(nextPage) { fetchResponse, fetchError ->
            hideLoading()

            fetchError?.let { error ->
                showRetryingPopup(error.errorDescription)
            }

            fetchResponse?.let { response ->
                retryingAttempt = 0

                nextPage = response.next

                combineLists(response.people)
            }
        }
    }

    private fun combineLists(newList: List<Person>) {
        val totalList: ArrayList<Person> = ArrayList()

        val currentList: MutableList<Person> = personRecyclerViewAdapter.differ.currentList

        totalList.addAll(currentList)
        totalList.addAll(newList)

        val linkedHashSet = LinkedHashSet<Person>(totalList)
        val uniqueList = ArrayList<Person>().apply {
            addAll(linkedHashSet)
        }
        personRecyclerViewAdapter.differ.submitList(uniqueList)
    }

    private fun showRetryingPopup(message: String) {

        val canRetry: Boolean = checkRetryingLimit()

        if (canRetry) {
            showPopupWithAction(message).setPositiveButton(getString(R.string.try_again)) { dialog, _ ->
                dialog.dismiss()

                if (nextPage == null) {
                    getInitializeData()
                } else {
                    getNextPageData()
                }

            }.show()
        } else {
            showErrorView()
        }
    }

    private fun checkRetryingLimit(): Boolean {
        return when (retryingAttempt) {
            retryingLimit -> {
                false
            }
            else -> {
                retryingAttempt++
                true
            }
        }
    }

    private fun showErrorView() {
        swipeRefreshRoot.isGone = true

        errorTextView.isGone = false
    }
}