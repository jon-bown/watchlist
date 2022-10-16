package edu.utap.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.api.Movie
import edu.utap.watchlist.databinding.MediaRowBinding

class MediaAdapter: RecyclerView.Adapter<MediaAdapter.VH>() {
     // Adapter does not have its own copy of list, it just observes
        private var movies = listOf<Movie>()

        // ViewHolder pattern minimizes calls to findViewById
        inner class VH(val binding: MediaRowBinding)
            : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val binding = MediaRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
            return VH(binding)
        }


        override fun onBindViewHolder(holder: VH, position: Int) {
            val binding = holder.binding
            movies[position].let{
                binding.mediaRow.text = it.title
                //binding.quoteText.text = it.quote
                //binding.charActText.text = it.characterActor
                //binding.movieText.text = it.movie
            }
        }

        fun submitList(items: List<Movie>) {
            movies = items
        }

        override fun getItemCount() = movies.size
    }
