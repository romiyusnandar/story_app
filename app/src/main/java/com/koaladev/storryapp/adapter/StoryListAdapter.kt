package com.koaladev.storryapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.koaladev.storryapp.R
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.databinding.ItemStoryBinding
import com.koaladev.storryapp.ui.view.DetailActivity

class StoryListAdapter : PagingDataAdapter<ListStoryItem, StoryListAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.apply {
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                createdAtText.text = story.createdAt

                Glide.with(ivItemPhoto.context)
                    .load(story.photoUrl)
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    val intent = Intent(root.context, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                        putExtra(DetailActivity.EXTRA_STORY_NAME, story.name)
                        putExtra(DetailActivity.EXTRA_STORY_DESCRIPTION, story.description)
                        putExtra(DetailActivity.EXTRA_STORY_PHOTO_URL, story.photoUrl)
                        putExtra(DetailActivity.EXTRA_STORY_CREATED_AT, story.createdAt)
                    }
                    root.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}