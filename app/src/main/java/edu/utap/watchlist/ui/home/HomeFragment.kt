package edu.utap.watchlist.ui.home

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
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
import java.util.Calendar.getInstance


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


    private var isLoading = false



    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }




    private fun initAdapter() {
        this.popularAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.nowPlayingAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.topRatedAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.trendingTodayAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.trendingWeekAdapter = MediaCardAdapter(viewModel, ::openMediaView)
    }

    private fun notifiyAdaptersChanged() {
        popularAdapter.notifyDataSetChanged()
        nowPlayingAdapter.notifyDataSetChanged()
        topRatedAdapter.notifyDataSetChanged()

    }

    fun openMediaView(item: MediaItem) {
        //open single view with given media item
        viewModel.setUpCurrentMediaData(item)
        //view?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.INVISIBLE


        val manager: FragmentManager? = parentFragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, MediaItemViewFragment.newInstance(), null)
        transaction.addToBackStack(null)
        transaction.commit()

        val act = activity as MainActivity
        act.hideNavBar()
//        childFragmentManager.beginTransaction()
//            .replace(R.id.container, MediaItemViewFragment.newInstance()).commit()

//        findNavController().navigate(MediaItemViewFragment.newInstance())
//        findNavController().navigate(HomeFragmentDirections.actionHomeToMedia(),
//                NavOptions.Builder().build())



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
        initRecyclerViewDividers(binding.popularList)

        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observePopularMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                popularAdapter.submitMediaList(movieList)
                popularAdapter.notifyDataSetChanged()

            })
        initPopularScrollListener()
    }


    fun initNowPlayingList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.nowPlayingList.layoutManager = manager
        binding.nowPlayingList.adapter = nowPlayingAdapter
        initRecyclerViewDividers(binding.nowPlayingList)

        viewModel.observeNowPlayingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                nowPlayingAdapter.submitMediaList(movieList)
                nowPlayingAdapter.notifyDataSetChanged()

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
        initRecyclerViewDividers(binding.nowPlayingList)

        viewModel.observeTopRatedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                topRatedAdapter.submitMediaList(movieList)
                topRatedAdapter.notifyDataSetChanged()

            })
        //Scroll listener
        initTopRatedScrollListener()
    }

    private fun initTrendingTodayList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.trendingTodayList.layoutManager = manager
        binding.trendingTodayList.adapter = trendingTodayAdapter
        initRecyclerViewDividers(binding.trendingTodayList)

        viewModel.observeTrendingTodayMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                trendingTodayAdapter.submitMediaList(movieList)
                trendingTodayAdapter.notifyDataSetChanged()

            })
        //Scroll listener
        //initTopRatedScrollListener()
    }

    private fun initTrendingWeekList() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.trendingWeekList.layoutManager = manager
        binding.trendingWeekList.adapter = trendingWeekAdapter
        initRecyclerViewDividers(binding.trendingWeekList)

        viewModel.observeTrendingWeekMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                trendingWeekAdapter.submitMediaList(movieList)
                trendingWeekAdapter.notifyDataSetChanged()
            })
        //Scroll listener
        //initTopRatedScrollListener()
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
            viewModel.updateMediaType(true)
            viewModel.netRefresh()


        }
        binding.opt2.setOnClickListener {
            binding.moviesTvControl.check(R.id.opt_2)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.opt1.setBackgroundColor(Color.TRANSPARENT)
            viewModel.updateMediaType(false)
            viewModel.netRefresh()
            popularAdapter.notifyDataSetChanged()

        }
        binding.moviesTvControl.check(R.id.opt_1)
        binding.opt1.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        binding.moviesTvControl.setOnCheckedChangeListener { radioGroup, i ->
            binding.moviesTvControl.check(i)
        }
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

    }

    private fun initTopRatedScrollListener() {

    }

    private fun initTrendingWeekScrollListener() {

    }

    private fun initTrendingDayScrollListener() {

    }

    private fun initUpcomingScrollListener() {

    }

}