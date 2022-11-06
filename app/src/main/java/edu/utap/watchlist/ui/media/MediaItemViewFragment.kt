package edu.utap.watchlist.ui.media

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.adapters.MediaCardAdapter
import edu.utap.watchlist.adapters.ProviderAdapter
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentMediaItemViewBinding
import edu.utap.watchlist.R


class MediaItemViewFragment : Fragment() {


    private var _binding: FragmentMediaItemViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var similarAdapter: MediaCardAdapter
    private lateinit var recommendedAdapter: MediaCardAdapter
    private lateinit var streamProvidersAdapter: ProviderAdapter
    private lateinit var buyProviderAdapter: ProviderAdapter
    private lateinit var rentProviderAdapter: ProviderAdapter

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun initAdapters(){
        similarAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        recommendedAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        streamProvidersAdapter = ProviderAdapter(viewModel)
        buyProviderAdapter = ProviderAdapter(viewModel)
        rentProviderAdapter = ProviderAdapter(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val imageView: ImageView = getView()!!.findViewById<View>(R.id.foo) as ImageView


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


        initAdapters()


            viewModel.observeCurrentMovie().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                (requireActivity() as AppCompatActivity).supportActionBar?.title = it.title
                        //binding.ratingText.text =
                //binding.runTimeText = it.run time

                initSeenButton(it.id.toString())

                if(it.backdropPath != null){

                    viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                }
            }


            viewModel.observeCurrentTV().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                Log.d("BACKDROP_PATH", it.backdropPath)
                viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                initSeenButton(it.id.toString())
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
        initRecommendedList()

        initProviderList()

        return root
    }

    fun initSeenButton(item: String){
        binding.seenButton.setOnClickListener {
            if(viewModel.checkInSeenMediaItems(item)){
                //item is seen
                Log.d("Contains", item)
                viewModel.removeSeenMedia(item)
            }
            else {
                viewModel.addSeenMedia(item)

            }

        }

        viewModel.observeSeenMediaItems().observe(viewLifecycleOwner) {
            Log.d("Seen Items Changed", it.toString())
            if(it.contains(item)){
                binding.seenButton.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
            }
            else {
                binding.seenButton.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            }


        }

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
        binding.similarItemList.adapter = similarAdapter

        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observeSimilarMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                similarAdapter.submitMediaList(movieList)
                similarAdapter.notifyDataSetChanged()

            })
    }


    fun initRecommendedList() {

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recommendedList.layoutManager = manager
        binding.recommendedList.adapter = recommendedAdapter


        viewModel.observeRecommendedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                recommendedAdapter.submitMediaList(movieList)
                recommendedAdapter.notifyDataSetChanged()
            })
    }

    fun initProviderList() {


        //Stream
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.streamServiceList.layoutManager = manager
        binding.streamServiceList.adapter = streamProvidersAdapter

        //rent
        val managerBuy = LinearLayoutManager(context)
        managerBuy.orientation = LinearLayoutManager.HORIZONTAL
        binding.buyServiceList.layoutManager = managerBuy
        binding.buyServiceList.adapter = buyProviderAdapter


        //buy
        val managerRent = LinearLayoutManager(context)
        managerRent.orientation = LinearLayoutManager.HORIZONTAL
        binding.rentServiceList.layoutManager = managerRent
        binding.rentServiceList.adapter = rentProviderAdapter


        viewModel.observeStreamingProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                streamProvidersAdapter.submitProviderList(providerList)
                streamProvidersAdapter.notifyDataSetChanged()
            })


        viewModel.observeBuyProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                buyProviderAdapter.submitProviderList(providerList)
                buyProviderAdapter.notifyDataSetChanged()
            })

        viewModel.observeRentProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                rentProviderAdapter.submitProviderList(providerList)
                rentProviderAdapter.notifyDataSetChanged()
            })



    }




    fun openMediaView(item: MediaItem) {
        viewModel.setUpCurrentMediaData(item)
        findNavController().popBackStack()
        findNavController().navigate(R.id.navigation_media)
    }




    override fun onDestroy() {
        super.onDestroy()

    }

}