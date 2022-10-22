package edu.utap.watchlist.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaCardBinding
import edu.utap.watchlist.ui.media.MediaItemView

class MediaCardAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<MediaCardAdapter.VH>()  {

    private var media = mutableListOf<MediaItem>()

    inner class VH(val binding: MediaCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            //XXX Write me.
            binding.root.setOnClickListener {

                //open media item fragment
                val intent = Intent(binding.root.context, MediaItemView::class.java).also {
                    //pass media item view

                }

                //launch activity from result launcher
                binding.root.context.startActivity(intent)

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
            //binding.TV.text = it.title
            //set image
            if(it.imageURL != null){
                viewModel.netFetchImage(binding.mediaImage, it.imageURL!!)
            }

        }
    }


    fun submitMediaList(items: List<MediaItem>) {
        media.clear()
        media.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = media.size


}