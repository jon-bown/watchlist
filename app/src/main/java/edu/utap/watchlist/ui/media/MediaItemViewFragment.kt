package edu.utap.watchlist.ui.media

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.MediaCardAdapter
import edu.utap.watchlist.adapters.ProviderAdapter
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentMediaItemViewBinding
import edu.utap.watchlist.ui.watchlist.WatchListCheckView


private const val CLOSE_FULL = "param1"

class MediaItemViewFragment : Fragment() {

    private var param1: String? = null

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

        arguments?.let {
            param1 = it.getString(CLOSE_FULL)
        }
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

        setCloseButton()


            viewModel.observeCurrentMovie().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                binding.runTimeText.text = convertHHMM(it.runtime)
                binding.popularityText.text = "${it.voteAverage.toDouble().format(1)}/10"
                binding.overviewText.text = it.overview
                binding.taglineText.text = it.tagline
                //if movie:
                binding.PerformanceText.text = "Budget: $${convertToMillions(it.budget)}m      Revenue: $${convertToMillions(it.revenue)}m"

                        //binding.ratingText.text =
                //binding.runTimeText = it.run time

                viewModel.fetchProviders()

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

                viewModel.fetchProviders()
                //set other properties
            }


        binding.addButton.setOnClickListener {
            //open watchlist selecitonview fragment

//            findNavController().navigate(
//                MediaItemViewFragmentDirections.actionMediaToWatchlist(),
//                NavOptions.Builder().setLaunchSingleTop(true).build())

            val manager: FragmentManager? = parentFragmentManager
            val transaction: FragmentTransaction = manager!!.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, WatchListCheckView.newInstance(), null)
            transaction.addToBackStack(null)
            transaction.commit()

            val act = activity as MainActivity
            act.hideNavBar()
            act.hideActionBar()
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
                binding.seenButton.setBackgroundResource(R.drawable.ic_baseline_check_box_24)
            }
            else {
                binding.seenButton.setBackgroundResource(R.drawable.ic_baseline_check_box_outline_blank_24)
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

        binding.streamContainer.visibility = View.GONE
        binding.buyContainer.visibility = View.GONE
        binding.rentContainer.visibility = View.GONE
        binding.howToWatchText.visibility = View.GONE


        viewModel.observeStreamingProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                if(!providerList.isEmpty()){
                    binding.streamContainer.visibility = View.VISIBLE
                    binding.howToWatchText.visibility = View.VISIBLE
                }
                else {
                    binding.streamContainer.visibility = View.GONE
                }
                streamProvidersAdapter.submitProviderList(providerList)
                streamProvidersAdapter.notifyDataSetChanged()
            })


        viewModel.observeBuyProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                if(!providerList.isEmpty()){
                    binding.buyContainer.visibility = View.VISIBLE
                    binding.howToWatchText.visibility = View.VISIBLE
                }
                else {
                    binding.buyContainer.visibility = View.GONE
                }
                buyProviderAdapter.submitProviderList(providerList)
                buyProviderAdapter.notifyDataSetChanged()
            })

        viewModel.observeRentProviders().observe(viewLifecycleOwner,
            Observer { providerList ->
                if(!providerList.isEmpty()){
                    binding.rentContainer.visibility = View.VISIBLE
                    binding.howToWatchText.visibility = View.VISIBLE
                }
                else {
                    binding.rentContainer.visibility = View.GONE
                }
                rentProviderAdapter.submitProviderList(providerList)
                rentProviderAdapter.notifyDataSetChanged()
            })

    }




    fun openMediaView(item: MediaItem) {
        viewModel.setUpCurrentMediaData(item)
        //findNavController().popBackStack()
        //findNavController().navigate(R.id.navigation_media)
        val manager: FragmentManager? = parentFragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, MediaItemViewFragment.newInstance("one"), null)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun setCloseButton() {


        binding.closeButton.setOnClickListener {


            if(viewModel.observeCurrentMediaItemsStack().value!!.size == 1){
//                val manager: FragmentManager? = parentFragmentManager
//
//                val backStackId = manager?.getBackStackEntryAt(0)!!.getId();
//
//                manager.popBackStack(backStackId,
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE)

                val manager: FragmentManager? = parentFragmentManager
                manager?.popBackStack()

                viewModel.popToLastMediaData()

                val act = activity as MainActivity
                act.showNavBar()
                act.showActionBar()
            }
            else {
                val manager: FragmentManager? = parentFragmentManager
                manager?.popBackStack()

                viewModel.popToLastMediaData()

            }

        }

    }

    private fun convertHHMM(minutes: Int): String {
        val seconds = minutes*60
        val HH = seconds / 3600.0
        val MM = seconds % 3600.0 / 60.0
        return "${HH.format(0)}h ${MM.format(0)}m"
    }


    private fun convertToMillions(dollars: Int): Int {
        val million = 1000000L
        return (dollars / million).toInt()
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            MediaItemViewFragment().apply {
                arguments = Bundle().apply{
                    putString(CLOSE_FULL, param1)
                }
            }
    }

}