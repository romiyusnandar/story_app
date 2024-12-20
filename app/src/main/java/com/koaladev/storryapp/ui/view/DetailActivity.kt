package com.koaladev.storryapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.koaladev.storryapp.R
import com.koaladev.storryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
        const val EXTRA_STORY_PHOTO_URL = "extra_story_photo_url"
        const val EXTRA_STORY_CREATED_AT = "extra_story_created_at"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayStoryDetails()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationIconTint(ContextCompat.getColor(this, R.color.md_theme_onPrimary))
    }

    private fun displayStoryDetails() {
        intent.apply {
            binding.tvAuthorName.text = getStringExtra(EXTRA_STORY_NAME)
            binding.tvDescriptionDetails.text = getStringExtra(EXTRA_STORY_DESCRIPTION)
            binding.tvCreatedAt.text = getStringExtra(EXTRA_STORY_CREATED_AT)
            Glide.with(this@DetailActivity)
                .load(getStringExtra(EXTRA_STORY_PHOTO_URL))
                .into(binding.ivDetailsPhoto)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}