package com.example.movie.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movie.data.model.remote.Movie
import com.example.movie.databinding.ImageItemBinding

enum class HeroAction { PLAY, ADD, NOTIFICATION }

class HomePagerAdapter(
    private val onAction: (Movie, HeroAction) -> Unit
) : ListAdapter<Movie, HomePagerAdapter.ImageVH>(ImageDiff()) {

    inner class ImageVH(val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) = with(binding) {
            movie = item
            executePendingBindings()

            buttonPlay.setOnClickListener { onAction(item, HeroAction.PLAY) }
            buttonMyList.setOnClickListener { onAction(item, HeroAction.ADD) }
            imageViewNotification.setOnClickListener { onAction(item, HeroAction.NOTIFICATION) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val li = LayoutInflater.from(parent.context)
        return ImageVH(ImageItemBinding.inflate(li, parent, false))
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) =
        holder.bind(getItem(position))

    private class ImageDiff : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(o: Movie, n: Movie) = o.id == n.id
        override fun areContentsTheSame(o: Movie, n: Movie) = o == n
    }
}
