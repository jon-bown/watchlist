package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.databinding.StringItemRowBinding

class StringListAdapter: RecyclerView.Adapter<StringListAdapter.VH>() {

    // Adapter does not have its own copy of list, it just observes
    private var items = mutableListOf<String>()

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(val binding: StringItemRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    //binding.checkMark.setImageResource()
                    //clear all images
                    //call click listener to update viewmodel
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = StringItemRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(binding)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        items[position].let{
            binding.itemName.text = it
        }
    }

    fun submitList(items: List<String>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}