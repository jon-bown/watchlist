package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.api.WatchList
import edu.utap.watchlist.databinding.MediaRowBinding
import edu.utap.watchlist.databinding.WatchlistRowBinding


class WatchListAdapter: RecyclerView.Adapter<WatchListAdapter.VH>() {
    // Adapter does not have its own copy of list, it just observes
    private var watchLists = mutableListOf<WatchList>()


    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(val binding: WatchlistRowBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = WatchlistRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(binding)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        watchLists[position].let{
            binding.watchListName.text = it.name
            //binding.quoteText.text = it.quote
            //binding.charActText.text = it.characterActor
            //binding.movieText.text = it.movie
        }
    }


    fun submitMediaList(items: List<WatchList>) {
        watchLists.clear()
        watchLists.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = watchLists.size
}
