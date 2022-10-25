package edu.utap.watchlist.ui.media

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.MediaCardAdapter
import edu.utap.watchlist.adapters.MediaWatchListItemAdapter
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentMediaItemViewBinding
import edu.utap.watchlist.databinding.FragmentProfileBinding
import edu.utap.watchlist.ui.home.HomeFragmentDirections

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MediaItemViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MediaItemViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var _binding: FragmentMediaItemViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: MediaCardAdapter

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMediaItemViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backdrop.setImageBitmap(null);
        //weird issue with leftover images
        adapter = MediaCardAdapter(viewModel, ::openMediaView)



            viewModel.observeCurrentMovie().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                if(it.backdropPath != null){
                    Log.d("BACKDROP_PATH", it.backdropPath)

                    viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                }

                //set other properties
            }


            viewModel.observeCurrentTV().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                Log.d("BACKDROP_PATH", it.backdropPath)
                viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                //set other properties
            }


        binding.addButton.setOnClickListener {
            //open watchlist selecitonview fragment
            val bundle = bundleOf("type" to "language")
            findNavController().navigate(
                MediaItemViewFragmentDirections.actionMediaToWatchlist(),
                NavOptions.Builder().setLaunchSingleTop(true).build())
            //findNavController().navigate(R.id.navigation_stringList, bundle)
        }



        //init items
        initSimilarList()

        return root
    }

    //Set up lists

    fun initSimilarList() {
        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.similarItemList.layoutManager = manager
        //val manager = StaggeredGridLayoutManager(-1,StaggeredGridLayoutManager.HORIZONTAL)
        //manager.orientation =
        binding.similarItemList.layoutManager = manager
        binding.similarItemList.adapter = adapter

        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observeSimilarMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                adapter.submitMediaList(movieList)
                adapter.notifyDataSetChanged()

            })
    }



    fun openMediaView(item: MediaItem) {
        viewModel.setUpCurrentMediaData(item)



        findNavController().navigate(R.id.navigation_media)
    }




    override fun onDestroy() {
        super.onDestroy()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MediaItemViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaItemViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}