package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.BuildConfig
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaRowBinding

//Used for search results
class MediaAdapter(private val clickListener: (item: MediaItem)->Unit): RecyclerView.Adapter<MediaAdapter.VH>() {
     // Adapter does not have its own copy of list, it just observes
        private var media = mutableListOf<MediaItem>()


        // ViewHolder pattern minimizes calls to findViewById
        inner class VH(val binding: MediaRowBinding)
            : RecyclerView.ViewHolder(binding.root) {
                init {
                    binding.root.setOnClickListener {
                        clickListener(media[adapterPosition])
                    }
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val binding = MediaRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
            return VH(binding)
        }


        override fun onBindViewHolder(holder: VH, position: Int) {
            val binding = holder.binding
            media[position].let{
                    binding.mediaRow.text = it.title
                }
            }


        fun submitMediaList(items: List<MediaItem>) {
            media.clear()
            media.addAll(items)
            notifyDataSetChanged()

        }



        override fun getItemCount() = media.size
    }
