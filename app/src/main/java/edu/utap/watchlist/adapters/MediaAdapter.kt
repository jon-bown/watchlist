package edu.utap.watchlist.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.MediaRowBinding
import edu.utap.watchlist.ui.media.MediaItemView

//Used for search results
class MediaAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<MediaAdapter.VH>() {
     // Adapter does not have its own copy of list, it just observes
        private var media = mutableListOf<MediaItem>()


        // ViewHolder pattern minimizes calls to findViewById
        inner class VH(val binding: MediaRowBinding)
            : RecyclerView.ViewHolder(binding.root) {
                init {
                    binding.root.setOnClickListener {
                        val intent = Intent(binding.root.context, MediaItemView::class.java).also { tent ->
                            if(viewModel.getMediaMode()){
                                tent.putExtra(MediaCardAdapter.TYPE, "Movie")
                            }
                            else {
                                tent.putExtra(MediaCardAdapter.TYPE, "TV")
                            }
                            tent.putExtra(MediaCardAdapter.ID, media[adapterPosition].id.toString())

                            tent.putExtra(MediaCardAdapter.LANG, viewModel.observeLanguageSetting().value)

                            tent.putExtra(MediaCardAdapter.COUNTRY, viewModel.observeCountrySetting().value)

                        }
                        //launch activity from result launcher
                        binding.root.context.startActivity(intent)
                    }
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val binding = MediaRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
            return VH(binding)
        }


        override fun onBindViewHolder(holder: VH, position: Int) {
            val binding = holder.binding
            media[position].let{
                    binding.mediaRow.text = it.title
                    //binding.quoteText.text = it.quote
                    //binding.charActText.text = it.characterActor
                    //binding.movieText.text = it.movie
                }
            }


        fun submitMediaList(items: List<MediaItem>) {
            media.clear()
            media.addAll(items)
            notifyDataSetChanged()
        }


        override fun getItemCount() = media.size
    }
