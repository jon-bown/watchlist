package edu.utap.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaCardBinding
import edu.utap.watchlist.databinding.StreamProviderRowBinding
import edu.utap.watchlist.providers.Provider

//List of streaming services
class ProviderAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<ProviderAdapter.VH>()   {

    private var providers = mutableListOf<Provider>()


    inner class VH(val binding: StreamProviderRowBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderAdapter.VH {
        val binding = StreamProviderRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        val holder = VH(binding)

        return holder
    }


    override fun onBindViewHolder(holder: ProviderAdapter.VH, position: Int) {
        val binding = holder.binding

        providers[position].let{
            //binding.TV.text = it.title
            //set image
            if(it.logoURL != null){
                viewModel.netFetchImage(binding.streamImage, it.logoURL!!)
            }
            binding.providerName.text = it.name

        }
    }

    fun submitProviderList(items: List<Provider>) {
        providers.clear()
        providers.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemCount() = providers.size

}