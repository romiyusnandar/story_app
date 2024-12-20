package com.koaladev.storryapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.storryapp.R
import com.koaladev.storryapp.adapter.StoryAdapter
import com.koaladev.storryapp.databinding.ActivityMainBinding
import com.koaladev.storryapp.ui.viewmodel.MainViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.koaladev.storryapp.adapter.LoadingStateAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        lifecycleScope.launch {
            delay(500)
            checkUserSession()
        }

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        setupAction()
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MainActivity", "Menu item clicked: ${item.itemId}")
        return when (item.itemId) {
            R.id.ic_map -> {
                Log.d("MainActivity", "Maps menu item clicked")
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.tvGreeting.text = getString(R.string.welcome_username, user.name)
                observeViewModel(user.token)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_STORY, story)
            }
            startActivity(intent)
        }

        val viewPool = RecyclerView.RecycledViewPool()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setRecycledViewPool(viewPool)
            adapter = this@MainActivity.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    this@MainActivity.adapter.retry()
                }
            )
        }

        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> showLoading(true)
                is LoadState.NotLoading -> showLoading(false)
                is LoadState.Error -> {
                    showLoading(false)
                    val error = loadState.refresh as LoadState.Error
                    Toast.makeText(this, "Error: ${error.error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel(token: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getPageStories(token).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.fabAdd.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
}