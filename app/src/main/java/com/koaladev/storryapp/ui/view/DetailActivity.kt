package com.koaladev.storryapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.storryapp.R
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var toolbar: MaterialToolbar
    companion object {
        const val EXTRA_STORY = "extra_story"
        const val EXTRA_STORY_ID = "extra_story_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIconTint(ContextCompat.getColor(this, R.color.md_theme_onPrimary))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        story?.let {
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivStory)
            binding.tvStoryName.text = it.name
            binding.tvStoryDescription.text = it.description
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}