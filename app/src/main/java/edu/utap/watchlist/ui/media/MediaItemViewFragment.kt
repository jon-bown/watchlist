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
import edu.utap.watchlist.api.Movie
import edu.utap.watchlist.api.TVShow
import edu.utap.watchlist.databinding.FragmentMediaItemViewBinding
import edu.utap.watchlist.ui.watchlist.WatchListCheckView


private const val CLOSE_FULL = "param1"

class MediaItemViewFragment : Fragment() {

    private var param1: String? = null

    private var _binding: FragmentMediaItemViewBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var similarAdapter: MediaCardAdapter
    private lateinit var recommendedAdapter: MediaCardAdapter
    private lateinit var streamProvidersAdapter: ProviderAdapter
    private lateinit var buyProviderAdapter: ProviderAdapter
    private lateinit var rentProviderAdapter: ProviderAdapter

    private var isLoading = false
    private var currentSimilarPage = 1
    private var currentRecommendedPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(CLOSE_FULL)
        }
    }

    private fun setLoadingListener() {
        viewModel.observeFetchDone().observe(viewLifecycleOwner) {
            isLoading = false
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaItemViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backdrop.setImageBitmap(null);

        initAdapters()

        setCloseButton()


            viewModel.observeCurrentMovie().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                binding.overviewText.text = it.overview
                binding.taglineText.text = it.tagline
                binding.performanceContainer.visibility = View.VISIBLE
                binding.PerformanceText.text = "Budget: $${convertToMillions(it.budget)}m      Revenue: $${convertToMillions(it.revenue)}m"
                binding.infoText.text = setMovieInfo(it)
                viewModel.fetchProviders()

                initSeenButton(it.id.toString())

                if(it.backdropPath != null){
                    viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                }
            }


            viewModel.observeCurrentTV().observe(viewLifecycleOwner) {
                binding.movieTitleText.text = it.title
                binding.overviewText.text = it.overview
                if(it.backdropPath != null){
                    viewModel.netFetchBackdropImage(binding.backdrop, it.backdropPath)
                }
                binding.taglineText.text = it.tagline
                binding.performanceContainer.visibility = View.GONE
                binding.infoText.text = setTVInfo(it)
                initSeenButton(it.id.toString())

                viewModel.fetchProviders()
            }


        binding.addButton.setOnClickListener {
            //open watchlist selecitonview fragment

            val manager: FragmentManager? = parentFragmentManager
            val transaction: FragmentTransaction = manager!!.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, WatchListCheckView.newInstance(), null)
            transaction.addToBackStack(null)
            transaction.commit()

            val act = activity as MainActivity
            act.hideNavBar()
            act.hideActionBar()
        }

        initSimilarList()
        initSimilarScrollListener()

        initRecommendedList()
        initRecommendedScrollListener()

        initProviderList()

        setLoadingListener()

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
                binding.seenButton.setImageResource(R.drawable.ic_baseline_check_box_24)
            }
            else {
                binding.seenButton.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
            }


        }

    }

    fun initSimilarList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.similarItemList.layoutManager = manager
        binding.similarItemList.adapter = similarAdapter

        viewModel.observeSimilarMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                similarAdapter.submitMediaList(movieList)

            })
    }


    fun initRecommendedList() {

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recommendedList.layoutManager = manager
        binding.recommendedList.adapter = recommendedAdapter


        viewModel.observeRecommendedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                if(movieList.isEmpty()){
                    binding.recommendedList.visibility = View.GONE
                    binding.recommendedText.visibility = View.GONE
                }
                else {
                    binding.recommendedList.visibility = View.VISIBLE
                    binding.recommendedText.visibility = View.VISIBLE
                    recommendedAdapter.submitMediaList(movieList)
                }

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
                    binding.howToWatchText.text = "How To Watch In: ${viewModel.observeCountrySetting().value}"
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
                    binding.howToWatchText.text = "How To Watch In: ${viewModel.observeCountrySetting().value}"
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
                    binding.howToWatchText.text = "How To Watch In: ${viewModel.observeCountrySetting().value}"
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


            if(viewModel.observeCurrentMediaItemsStack().value!!.size <= 1){
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


    private fun initSimilarScrollListener() {
        binding.similarItemList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentSimilarPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeSimilarMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentSimilarPage+=1
                            viewModel.fetchSimilar(currentSimilarPage)
                        }
                    }

                }
            }
        })
    }


    private fun initRecommendedScrollListener() {
        binding.recommendedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentRecommendedPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeRecommendedMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentRecommendedPage+=1
                            viewModel.fetchRecommended(currentRecommendedPage)
                        }
                    }

                }
            }
        })
    }



    private fun setMovieInfo(movie: Movie): String{
        val infoStr = "Status: ${movie.status}\n" +
                "Release Date: ${movie.releaseDate}\n" +
                "Runtime: ${convertHHMM(movie.runtime)}"
                "User Score: ${movie.voteAverage.toDouble().format(1)}/10\n" +
                "Original Language: ${movie.originalLanguage}\n"
        return infoStr
    }

    private fun setTVInfo(tv: TVShow): String {
        val infoStr = "Status: ${tv.status}\n" +
                "User Score: ${tv.voteAverage.toDouble().format(1)}/10" +
                "First Air Date: ${tv.firstAirDate}\n" +
                "Last Air Date: ${tv.lastAirDate}\n" +
                "Original Language: ${tv.originalLanguage}\n"
        return infoStr
    }

    private fun convertHHMM(minutes: Int): String {
        val seconds = minutes*60
        val HH = seconds / 3600.0
        val MM = seconds % 3600.0 / 60.0
        return "${HH.format(0)}h ${MM.format(0)}m"
    }


    private fun convertToMillions(dollars: Int): String {
        val million = 1000000L

        val result = (dollars / million).toInt()
        if(result == 0){
            return " < 1m"
        }
        else {
          return result.toString()
        }

    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)


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