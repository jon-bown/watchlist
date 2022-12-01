package edu.utap.watchlist.adapters

import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.R
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.StringItemRowBinding

class StringListAdapter(private val clickListener: (selectedLists: List<String>, selection: String) -> Unit, private val allowCheckMarks: Boolean): RecyclerView.Adapter<StringListAdapter.VH>() {

    private var items = mutableListOf<String>()
    private var selectedItems = mutableListOf<String>()

    inner class VH(val binding: StringItemRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(allowCheckMarks) {
                    if(binding.checkMark.isSelected){
                        binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                        binding.checkMark.isSelected = false
                        selectedItems.remove(binding.itemName.text)
                    }
                    else {
                        binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_24_blue)
                        binding.checkMark.isSelected = true
                        selectedItems.add(binding.itemName.text.toString())
                    }
                }
                clickListener(selectedItems, items[adapterPosition])
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
            if(it in selectedItems){
                binding.checkMark.isSelected = true
                binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_24_blue)
            }
            else{
                binding.checkMark.isSelected = false
                binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
            }
        }
    }

    fun submitList(items: List<String>, selectedItems: List<String>) {
        if(selectedItems != null) {
            this.selectedItems.clear()
            this.selectedItems.addAll(selectedItems)
        }
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}