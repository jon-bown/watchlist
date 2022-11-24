package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.databinding.WatchlistListRowBinding

//displays the name of the watchlist to the user
class WatchListAdapter(private val viewModel: MainViewModel, private val clickListener: (selection: String) -> Unit): RecyclerView.Adapter<WatchListAdapter.VH>() {

    private var watchLists = mutableListOf<String>()


    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(val binding: WatchlistListRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    clickListener(watchLists[adapterPosition])
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
        watchLists[position].let{
            binding.itemName.text = it
        }
    }


    fun submitWatchListNames(items: List<String>) {
        watchLists.clear()
        watchLists.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = watchLists.size


    fun removeAt(position: Int){
        viewModel.removeWatchList(watchLists[position])
        notifyItemChanged(position)
    }

}