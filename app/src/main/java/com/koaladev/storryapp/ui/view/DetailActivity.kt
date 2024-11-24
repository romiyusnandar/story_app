package com.koaladev.storryapp.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.koaladev.storryapp.R
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        story?.let {
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.iv)
            binding.tvStoryName.text = it.name
            binding.tvStoryDescription.text = it.description
            binding.tvStoryCreatedAt.text = it.createdAt
            binding.tvStoryLocation.text = "Lat: ${it.lat}, Lon: ${it.lon}"
        }
    }
}