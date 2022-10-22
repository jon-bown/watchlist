package edu.utap.watchlist.adapters

import android.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaCardBinding
import edu.utap.watchlist.ui.media.MediaItemView
import edu.utap.watchlist.ui.mediaitem.MediaFragment


class MediaCardAdapter(private val viewModel: MainViewModel, private val owner: LifecycleOwner)
    : RecyclerView.Adapter<MediaCardAdapter.VH>()  {

    companion object keys {
        const val TYPE = "mediaType"
        const val ID = "mediaID"
        const val LANG = "language"
        const val COUNTRY = "country"

    }

    private var media = mutableListOf<MediaItem>()
    private var languageSetting = ""



    inner class VH(val binding: MediaCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            //XXX Write me.
            binding.root.setOnClickListener {


                val intent = Intent(binding.root.context, MediaItemView::class.java).also { tent ->
                    tent.putExtra(keys.TYPE, media[adapterPosition].mediaType)
                    tent.putExtra(keys.ID, media[adapterPosition].id.toString())

                    tent.putExtra(keys.LANG, viewModel.observeLanguageSetting().value)

                    tent.putExtra(keys.COUNTRY, viewModel.observeCountrySetting().value)

                }
                //launch activity from result launcher
                binding.root.context.startActivity(intent)
                //clickListener(media[adapterPosition])
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