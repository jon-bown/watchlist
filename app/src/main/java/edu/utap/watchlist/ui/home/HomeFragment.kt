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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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


    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        viewModel.observeFetchDone().observe(viewLifecycleOwner) {
            if(it){
                swipe.isRefreshing = false
            }

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.netRefresh()
            swipe.isRefreshing = true
        }
    }

    fun initPopularList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.popularList.layoutManager = manager
        binding.popularList.adapter = popularAdapter

        viewModel.observePopularMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                popularAdapter.submitMediaList(movieList)

            })
        initPopularScrollListener()
    }


    fun initNowPlayingList() {

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.nowPlayingList.layoutManager = manager
        binding.nowPlayingList.adapter = nowPlayingAdapter

        viewModel.observeNowPlayingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                nowPlayingAdapter.submitMediaList(movieList)

            })

        initNowPlayingScrollListener()
    }

    fun initTopRatedList() {

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.topRatedList.layoutManager = manager
        binding.topRatedList.adapter = topRatedAdapter

        viewModel.observeTopRatedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                topRatedAdapter.submitMediaList(movieList)

            })
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
        initTrendingWeekScrollListener()
    }


    fun initUpcomingList() {


        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.upcomingList.layoutManager = manager
        binding.upcomingList.adapter = upcomingAdapter

        viewModel.observeUpcomingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                Log.d("UPCOMING ITEMS", movieList.toString())
                upcomingAdapter.submitMediaList(movieList)
            })
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

        initSwipeLayout(binding.swipeRefreshLayout)

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
            resetScrollers()


        }
        binding.opt2.setOnClickListener {
            binding.moviesTvControl.check(R.id.opt_2)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.opt1.setBackgroundColor(Color.TRANSPARENT)
            viewModel.updateMovieMode(false)
            viewModel.netRefresh()
            resetScrollers()

        }

        binding.opt1.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))


    }

    private fun resetScrollers(){
        binding.popularList.scrollToPosition(0)
        binding.nowPlayingList.scrollToPosition(0)
        binding.upcomingList.scrollToPosition(0)
        binding.trendingTodayList.scrollToPosition(0)
        binding.trendingWeekList.scrollToPosition(0)
        binding.topRatedList.scrollToPosition(0)
    }

    //SCROLLING LISTENERS
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
                            (viewModel.observeUpcomingMediaItems().value!!.size - 7)) {
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