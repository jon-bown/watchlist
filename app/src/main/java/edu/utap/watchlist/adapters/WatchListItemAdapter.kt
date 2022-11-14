package edu.utap.watchlist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.WatchlistItemRowBinding

//displays each media item associated with a watchlist to the user
class WatchListItemAdapter(private val viewModel: MainViewModel, private val clickListener: (item: MediaItem)->Unit): RecyclerView.Adapter<WatchListItemAdapter.VH>() {
    // Adapter does not have its own copy of list, it just observes
    private var watchListItems = mutableListOf<MediaItem>()


    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(val binding: WatchlistItemRowBinding)
        : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.root.setOnClickListener {


                    //launch activity from result launcher
                    //findNavController()

                    clickListener(watchListItems[adapterPosition])
                }

                binding.root.setOnDragListener { view, dragEvent ->
                    Log.d("DRAG", "EVENT")
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
            //binding.quoteText.text = it.quote
            //binding.charActText.text = it.characterActor
            //binding.movieText.text = it.movie
            if(viewModel.checkInSeenMediaItems(it.id.toString())){
                binding.seenClick.setImageResource(R.drawable.ic_baseline_check_circle_24)
            }
            else {
                binding.seenClick.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            }


            binding.seenClick.setOnClickListener { view ->
                Log.d("CLICK ROW", "")
                if(viewModel.checkInSeenMediaItems(it.id.toString())){
                    //item is seen
                    viewModel.removeSeenMedia(it.id.toString())
                    binding.seenClick.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                }
                else {
                    viewModel.addSeenMedia(it.id.toString())
                    binding.seenClick.setImageResource(R.drawable.ic_baseline_check_circle_24)
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
}
