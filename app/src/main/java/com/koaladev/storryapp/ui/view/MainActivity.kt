package com.koaladev.storryapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.storryapp.R
import com.koaladev.storryapp.adapter.LoadingStateAdapter
import com.koaladev.storryapp.adapter.StoryAdapter
import com.koaladev.storryapp.databinding.ActivityMainBinding
import com.koaladev.storryapp.ui.viewmodel.MainViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        observeViewModel()
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
                viewModel.getStories(user.token)
            }
        }
    }
    private fun setupRecyclerView() {
        adapter = StoryAdapter(
            ::navigateToDetail
        )

        with(binding) {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter{ adapter.retry() }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { stories ->
            adapter.submitData(lifecycle, stories)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
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

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_STORY_ID, id)
        }
        startActivity(intent)
    }
}