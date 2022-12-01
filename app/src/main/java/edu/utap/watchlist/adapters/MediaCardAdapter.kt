package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaCardBinding


class MediaCardAdapter(private val viewModel: MainViewModel, private val clickListener: (item: MediaItem)->Unit)
    : RecyclerView.Adapter<MediaCardAdapter.VH>()  {

    companion object keys {
        const val TYPE = "mediaType"
        const val ID = "mediaID"
        const val LANG = "language"
        const val COUNTRY = "country"

    }

    private var media = mutableListOf<MediaItem>()
    private var languageSetting = ""



    inner class VH(val binding: MediaCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                clickListener(media[adapterPosition])
            }

        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = MediaCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        val holder = VH(binding)

        return holder
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding

        media[position].let{
            viewModel.netFetchImage(binding.mediaImage, it.imageURL)

        }
    }


    fun submitMediaList(items: List<MediaItem>) {
        media.clear()
        media.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = media.size


}