package edu.utap.watchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaCardBinding

class MediaCardAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<MediaCardAdapter.VH>()  {

    private var media = mutableListOf<MediaItem>()

    inner class VH(val binding: MediaCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            //XXX Write me.
            binding.root.setOnClickListener {

                //open media item fragment
            }

        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaCardAdapter.VH {
        val binding = MediaCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        val holder = VH(binding)

        return holder
    }


    override fun onBindViewHolder(holder: MediaCardAdapter.VH, position: Int) {
        val binding = holder.binding

        media[position].let{
            binding.TV.text = it.title
            //set image
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