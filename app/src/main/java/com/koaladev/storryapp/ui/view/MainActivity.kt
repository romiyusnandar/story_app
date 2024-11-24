package com.koaladev.storryapp.ui.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.koaladev.storryapp.R
import com.koaladev.storryapp.adapter.StoryAdapter
import com.koaladev.storryapp.databinding.ActivityMainBinding
import com.koaladev.storryapp.ui.viewmodel.MainViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primaryContainer)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.tvGreeting.text = getString(R.string.welcome_username, user.name)
            }
        }

        setupAction()
        setupRecyclerView()
        viewModel.getStories(false)
        observeViewModel()
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
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
}