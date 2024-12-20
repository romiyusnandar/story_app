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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.storryapp.R
import com.koaladev.storryapp.adapter.StoryListAdapter
import com.koaladev.storryapp.databinding.ActivityMainBinding
import com.koaladev.storryapp.ui.LoadingStateAdapter
import com.koaladev.storryapp.ui.viewmodel.MainViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var storyAdapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFabAction()
        setupLogoutAction()
        observeUserSession()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryListAdapter()

        binding.rvStory.apply {
            adapter = ConcatAdapter(
                storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { storyAdapter.retry() }
                )
            )
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                initialPrefetchItemCount = 5
            }
            itemAnimator = null

        }

        storyAdapter.addLoadStateListener { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            showLoading(isLoading)
            handleLoadStateError(loadState)
        }
    }

    private fun setupFabAction() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupLogoutAction() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeUserSession() {
        viewModel.userSession.observe(this) { user ->
            if (user.isLogin) {
                binding.tvGreeting.text = getString(R.string.welcome_username, user.name)
                observeStories(user.token)
            } else {
                navigateToWelcomeScreen()
            }
        }
    }

    private fun observeStories(token: String) {
        lifecycleScope.launch {
            viewModel.getStories(token).collectLatest {
                storyAdapter.submitData(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            fabAdd.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun handleLoadStateError(loadState: androidx.paging.CombinedLoadStates) {
        val errorState = listOf(
            loadState.source.append,
            loadState.source.prepend,
            loadState.append,
            loadState.prepend
        ).find { it is LoadState.Error } as? LoadState.Error

        errorState?.let {
            Toast.makeText(this, it.error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToWelcomeScreen() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ic_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
