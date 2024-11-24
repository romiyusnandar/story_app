package com.koaladev.storryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.databinding.ItemViewStoryBinding

class StoryAdapter (private val stories: List<ListStoryItem?>, private val onItemClick: (ListStoryItem) -> Unit) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: ItemViewStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                tvEventName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(iv)
            }
            binding.root.setOnClickListener { onItemClick(story) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemViewStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        if (story != null) {
            holder.bind(story)
        }
    }
}