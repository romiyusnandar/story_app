package com.koaladev.storryapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.storryapp.R
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

        setupAction()
        setupRecyclerView()
        observeViewModel()
    }

    private fun checkUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.tvGreeting.text = getString(R.string.welcome_username, user.name)
                viewModel.getStories(false)
            }
        }
    }
    private fun setupRecyclerView() {
        adapter = StoryAdapter(emptyList()) { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_STORY, story)
            }
            startActivity(intent)
        }
        binding.rvStory.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { stories ->
            adapter = stories?.let {
                StoryAdapter(it) { story ->
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_STORY, story)
                    }
                    startActivity(intent)
            }
        }!!
            binding.rvStory.adapter = adapter
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
}