package com.pratama.dicodingstory.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pratama.dicodingstory.R
import com.pratama.dicodingstory.data.local.entity.Story
import com.pratama.dicodingstory.databinding.ActivityDetailStoryBinding
import com.pratama.dicodingstory.utils.setLocalDateFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Wait until all resource is already loaded
        supportPostponeEnterTransition()

        val story = intent.getParcelableExtra<Story>(EXTRA_DETAIL)
        parseStoryData(story)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun parseStoryData(story: Story?) {
        if (story != null) {
            binding.apply {
                tvStoryDescription.text = story.description
                toolbar.title = getString(R.string.detail_toolbar_title, story.name)
                tvStoryDate.setLocalDateFormat(story.createdAt)

                Glide
                    .with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }
                    })
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.no_data)
                    .into(ivStoryImage)
            }
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}