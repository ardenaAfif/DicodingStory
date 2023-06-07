package com.pratama.dicodingstory.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pratama.dicodingstory.data.local.entity.Story
import com.pratama.dicodingstory.databinding.StoryItemBinding
import com.pratama.dicodingstory.ui.detail.DetailStoryActivity
import com.pratama.dicodingstory.ui.detail.DetailStoryActivity.Companion.EXTRA_DETAIL
import com.pratama.dicodingstory.utils.setImageFromUrl
import com.pratama.dicodingstory.utils.setLocalDateFormat


class StoryListAdapter :
    PagingDataAdapter<Story, StoryListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDescription.text = story.description
                ivStoryImage.setImageFromUrl(context, story.photoUrl)
                tvStoryDate.setLocalDateFormat(story.createdAt)

                // On item clicked
                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStoryImage, "story_image"),
                            Pair(tvStoryUsername, "username"),
                            Pair(tvStoryDate, "date"),
                            Pair(tvStoryDescription, "description")
                        )

                    Intent(context, DetailStoryActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }

}