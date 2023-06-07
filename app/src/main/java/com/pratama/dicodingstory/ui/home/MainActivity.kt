package com.pratama.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pratama.dicodingstory.R
import com.pratama.dicodingstory.adapter.LoadingAdapter
import com.pratama.dicodingstory.adapter.StoryListAdapter
import com.pratama.dicodingstory.data.local.entity.Story
import com.pratama.dicodingstory.databinding.ActivityMainBinding
import com.pratama.dicodingstory.ui.auth.AuthActivity
import com.pratama.dicodingstory.ui.create.CreateStoryActivity
import com.pratama.dicodingstory.ui.maps.LocationActivity
import com.pratama.dicodingstory.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter

    private var token: String = ""
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        setSwipeRefreshLayout()
        setRecyclerView()
        getAllStories()

        binding.fabCreateStory.setOnClickListener {
            Intent(this, CreateStoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.saveAuthToken("")
                Intent(this, AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
                true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_location -> {
                Intent(this, LocationActivity::class.java).also { intent ->
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getAllStories() {
        viewModel.getAllStories(token).observe(this) { result ->
            updateRecyclerViewData(result)
        }
    }

//    private fun getAllStories() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                viewModel.getAllStories(token).observe(this) { result ->
//                    updateRecyclerViewData(result)
//                }
//            }
//        }
//    }

    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        listAdapter = StoryListAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                // List empty or error
                binding.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                // List not empty
                binding.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }

            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding.rvStories
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        listAdapter.submitData(lifecycle, stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}