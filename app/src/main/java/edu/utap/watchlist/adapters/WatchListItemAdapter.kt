package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.WatchlistItemRowBinding

//displays each media item associated with a watchlist to the user
class WatchListItemAdapter(private val viewModel: MainViewModel, private val clickListener: (item: MediaItem)->Unit): RecyclerView.Adapter<WatchListItemAdapter.VH>() {
    private var watchListItems = mutableListOf<MediaItem>()

    inner class VH(val binding: WatchlistItemRowBinding)
        : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.root.setOnClickListener {
                    clickListener(watchListItems[adapterPosition])
                }

                binding.root.setOnDragListener { view, dragEvent ->
                    true
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = WatchlistItemRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(binding)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding

        watchListItems[holder.adapterPosition].let{
            binding.watchListName.text = it.title
            if(viewModel.checkInSeenMediaItems(it.id.toString())){
                binding.seenClick.setImageResource(R.drawable.ic_baseline_check_box_24)
            }
            else {
                binding.seenClick.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
            }


            binding.seenClick.setOnClickListener { view ->
                if(viewModel.checkInSeenMediaItems(it.id.toString())){
                    //item is seen
                    viewModel.removeSeenMedia(it.id.toString())
                    binding.seenClick.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                }
                else {
                    viewModel.addSeenMedia(it.id.toString())
                    binding.seenClick.setImageResource(R.drawable.ic_baseline_check_box_24)
                }

            }

            if(it.imageURL != null){
                viewModel.netFetchImage(binding.poster, it.imageURL!!)
            }
        }

    }


    fun submitMediaList(items: List<MediaItem>) {
        watchListItems.clear()
        watchListItems.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = watchListItems.size

    fun removeAt(position: Int){
        viewModel.removeFromCurrentWatchList(watchListItems[position])
        notifyDataSetChanged()
    }
}
