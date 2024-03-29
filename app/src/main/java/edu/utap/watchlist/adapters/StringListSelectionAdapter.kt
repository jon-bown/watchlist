package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.watchlist.R
import edu.utap.watchlist.databinding.StringItemRowBinding

class StringListSelectionAdapter(private val clickListener: (selection: String)->Unit): RecyclerView.Adapter<StringListSelectionAdapter.VH>() {
    private var items = mutableListOf<String>()
    private var selectedItem = ""

    inner class VH(val binding: StringItemRowBinding)
        : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_24_blue)
                    selectedItem = items[adapterPosition]
                    clickListener(selectedItem)
                    notifyDataSetChanged()
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
            if(it == selectedItem) {
                binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_24_blue)
            }
            else {
                binding.checkMark.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)

            }
        }
    }

    fun submitList(items: List<String>, currentItem: String) {
        this.items.clear()
        this.items.addAll(items)
        this.selectedItem = currentItem
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}