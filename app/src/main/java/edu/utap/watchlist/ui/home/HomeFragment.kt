package edu.utap.watchlist.ui.home

import android.graphics.Color
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
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentHomeBinding
import edu.utap.watchlist.ui.media.MediaItemViewFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private var listDisplayType: String = "popular"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var popularAdapter: MediaCardAdapter
    private var currentPopularPage = 1
    private lateinit var nowPlayingAdapter: MediaCardAdapter
    private var currentNowPlayingPage = 1
    private lateinit var topRatedAdapter: MediaCardAdapter
    private var currentTopRatedPage = 1
    private lateinit var trendingTodayAdapter: MediaCardAdapter
    private var currentTrendingTodayPage = 1
    private lateinit var trendingWeekAdapter: MediaCardAdapter
    private var currentTrendingWeekPage = 1
    private lateinit var upcomingAdapter: MediaCardAdapter
    private var currentUpcomingPage = 1


    private var isLoading = false





    private fun initAdapter() {
        this.popularAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.nowPlayingAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.topRatedAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.trendingTodayAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.trendingWeekAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.upcomingAdapter = MediaCardAdapter(viewModel, ::openMediaView)

    }



    fun openMediaView(item: MediaItem) {
        //open single view with given media item
        viewModel.setUpCurrentMediaData(item)

        val manager: FragmentManager? = parentFragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, MediaItemViewFragment.newInstance("none"), null)
        transaction.addToBackStack(null)
        transaction.commit()

        val act = activity as MainActivity
        act.hideNavBar()
        act.hideActionBar()

    }






    private fun setLoadingListener() {
        viewModel.observeFetchDone().observe(viewLifecycleOwner) {
            isLoading = false
        }
    }

    fun initPopularList() {
        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.popularList.layoutManager = manager
        binding.popularList.adapter = popularAdapter
        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observePopularMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                popularAdapter.submitMediaList(movieList)

            })
        initPopularScrollListener()
    }


    fun initNowPlayingList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.nowPlayingList.layoutManager = manager
        binding.nowPlayingList.adapter = nowPlayingAdapter

        viewModel.observeNowPlayingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                nowPlayingAdapter.submitMediaList(movieList)

            })

        //Scroll listener
        initNowPlayingScrollListener()
    }

    fun initTopRatedList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.topRatedList.layoutManager = manager
        binding.topRatedList.adapter = topRatedAdapter

        viewModel.observeTopRatedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                topRatedAdapter.submitMediaList(movieList)

            })
        //Scroll listener
        initTopRatedScrollListener()
    }

    private fun initTrendingTodayList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.trendingTodayList.layoutManager = manager
        binding.trendingTodayList.adapter = trendingTodayAdapter

        viewModel.observeTrendingTodayMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                trendingTodayAdapter.submitMediaList(movieList)

            })
        //Scroll listener
        initTrendingDayScrollListener()
    }

    private fun initTrendingWeekList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.trendingWeekList.layoutManager = manager
        binding.trendingWeekList.adapter = trendingWeekAdapter
        viewModel.observeTrendingWeekMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                trendingWeekAdapter.submitMediaList(movieList)
            })
        //Scroll listener
        initTrendingWeekScrollListener()
    }


    fun initUpcomingList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.upcomingList.layoutManager = manager
        binding.upcomingList.adapter = upcomingAdapter

        viewModel.observeUpcomingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                upcomingAdapter.submitMediaList(movieList)
            })
        //Scroll listener
        initUpcomingScrollListener()
    }


    override fun onResume() {
        super.onResume()
        if(viewModel.observeMovieMode().value!!){
            binding.moviesTvControl.check(R.id.opt_1)
            binding.opt2.setBackgroundColor(Color.TRANSPARENT)
            binding.opt1.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        }
        else {
            binding.moviesTvControl.check(R.id.opt_2)
            binding.opt1.setBackgroundColor(Color.TRANSPARENT)
            binding.opt2.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        if(savedInstanceState == null) {
            viewModel.populateUserData()
        }

        initMainSelector()

        //Start Lists
        initAdapter()
        initPopularList()
        initNowPlayingList()
        initTopRatedList()
        initTrendingTodayList()
        initTrendingWeekList()
        initUpcomingList()

        viewModel.observeMovieMode().observe(viewLifecycleOwner) {
            if(it){
                binding.nowPlayingText.text = "Now Playing"
                binding.upcomingText.text = "Upcoming"
                binding.moviesTvControl.check(R.id.opt_1)


            }
            else {
                binding.nowPlayingText.text = "Now Airing"
                binding.upcomingText.text = "Airing Today"
                binding.moviesTvControl.check(R.id.opt_2)
            }
        }


        setLoadingListener()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initMainSelector() {
        binding.opt1.setOnClickListener {
            binding.moviesTvControl.check(R.id.opt_1)
            binding.opt2.setBackgroundColor(Color.TRANSPARENT)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            viewModel.updateMovieMode(true)
            viewModel.netRefresh()


        }
        binding.opt2.setOnClickListener {
            binding.moviesTvControl.check(R.id.opt_2)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.opt1.setBackgroundColor(Color.TRANSPARENT)
            viewModel.updateMovieMode(false)
            viewModel.netRefresh()
            popularAdapter.notifyDataSetChanged()

        }

        binding.opt1.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))


    }



    //SCROLLING LISTENERS
    private fun addScrollListener(list: RecyclerView, page: Int, size: Int, fetch: (page: Int) -> Unit) {

//        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                if (!isLoading) {
//                    if(page < 10) {
//                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
//                            (size - 5)) {
//                            isLoading = true
//                            page+=1
//                            fetch(currentPopularPage)
//                        }
//                    }
//
//                }
//            }
//        })




    }





    private fun initPopularScrollListener() {
        binding.popularList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentPopularPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observePopularMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentPopularPage+=1
                            viewModel.fetchPopular(currentPopularPage)
                        }
                    }

                }
            }
        })
    }

    private fun initNowPlayingScrollListener() {
        binding.nowPlayingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentNowPlayingPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeNowPlayingMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentNowPlayingPage+=1
                            viewModel.fetchNowPlaying(currentNowPlayingPage)
                        }
                    }

                }
            }
        })
    }

    private fun initTopRatedScrollListener() {
        binding.topRatedList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentTopRatedPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeTopRatedMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentTopRatedPage+=1
                            viewModel.fetchTopRated(currentTopRatedPage)
                        }
                    }

                }
            }
        })
    }

    private fun initTrendingWeekScrollListener() {
        binding.trendingWeekList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentTrendingWeekPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeTrendingWeekMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentTrendingWeekPage+=1
                            viewModel.fetchTrendingWeek(currentTrendingWeekPage)
                        }
                    }

                }
            }
        })
    }

    private fun initTrendingDayScrollListener() {
        binding.trendingTodayList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentTrendingTodayPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeTrendingTodayMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentTrendingTodayPage+=1
                            viewModel.fetchTrendingToday(currentTrendingTodayPage)
                        }
                    }

                }
            }
        })
    }

    private fun initUpcomingScrollListener() {
        binding.upcomingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentUpcomingPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeUpcomingMediaItems().value!!.size - 5)) {
                            isLoading = true
                            currentUpcomingPage+=1
                            viewModel.fetchUpcoming(currentUpcomingPage)
                        }
                    }

                }
            }
        })
    }


}