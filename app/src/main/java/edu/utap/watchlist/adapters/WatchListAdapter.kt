package edu.utap.watchlist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItems
import edu.utap.watchlist.api.WatchList
import edu.utap.watchlist.databinding.WatchlistListRowBinding

//displays the name of the watchlist to the user
class WatchListAdapter(private val viewModel: MainViewModel, private val clickListener: (selection: String) -> Unit): RecyclerView.Adapter<WatchListAdapter.VH>() {

    private var watchLists = mutableListOf<WatchList>()

    inner class VH(val binding: WatchlistListRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    clickListener(watchLists[adapterPosition].name!!)
                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = WatchlistListRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(binding)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        watchLists[position].let{ watchList ->
            binding.itemName.text = watchList.name
            val list = watchList.items!!.map {
                viewModel.checkInSeenMediaItems(it.id.toString()) }
            val size = watchList.items!!.size.toDouble()
            val count = list!!.count { it }.toDouble()
            if(size > 0) {
                binding.progressBar.progress = ((count / size)*100.0).toInt()
            }
            else {
                binding.progressBar.progress = 0
            }


        }
    }


    fun submitWatchListNames(items: List<WatchList>) {
        watchLists.clear()
        watchLists.addAll(items)
        notifyDataSetChanged()
    }



    override fun getItemCount() = watchLists.size


    fun removeAt(position: Int){
        viewModel.removeWatchList(watchLists[position].name!!)
        notifyItemChanged(position)
    }

}