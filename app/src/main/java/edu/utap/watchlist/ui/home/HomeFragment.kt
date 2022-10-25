package edu.utap.watchlist.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentHomeBinding


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



    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }




    private fun initAdapter() {
        //addListToAdapter()
        //this.adapter = MediaAdapter()
        this.popularAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.nowPlayingAdapter = MediaCardAdapter(viewModel, ::openMediaView)
        this.topRatedAdapter = MediaCardAdapter(viewModel, ::openMediaView)

    }

    private fun notifiyAdaptersChanged() {
        popularAdapter.notifyDataSetChanged()
        nowPlayingAdapter.notifyDataSetChanged()
        topRatedAdapter.notifyDataSetChanged()
    }

    fun openMediaView(item: MediaItem) {
        viewModel.setUpCurrentMediaData(item)


        findNavController().navigate(HomeFragmentDirections.actionHomeToMedia(),
                NavOptions.Builder().setLaunchSingleTop(true).build())
    }



    fun initPopularList() {
        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.popularList.layoutManager = LinearLayoutManager(activity)

        //val manager = StaggeredGridLayoutManager(-1,StaggeredGridLayoutManager.HORIZONTAL)
        //manager.orientation =
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
        initScrollListener()




    }

    //add observer to reset this
    private var isLoading = false

    private fun initScrollListener() {
        binding.popularList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if(currentPopularPage < 10) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                            (viewModel.observePopularMediaItems().value!!.size - 1)) {
                            //bottom of list!
                            Log.d("SCROLLING", "LOAD")
                            //loadMore()
                            isLoading = true
                            currentPopularPage+=1
                        }
                    }

                }
            }
        })
    }


    fun initNowPlayingList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.nowPlayingList.layoutManager = LinearLayoutManager(activity)

        binding.nowPlayingList.layoutManager = manager
        binding.nowPlayingList.adapter = nowPlayingAdapter
        initRecyclerViewDividers(binding.nowPlayingList)

        viewModel.observeNowPlayingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                nowPlayingAdapter.submitMediaList(movieList)
                nowPlayingAdapter.notifyDataSetChanged()

            })


    }

    fun initTopRatedList() {

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.topRatedList.layoutManager = LinearLayoutManager(activity)

        binding.topRatedList.layoutManager = manager
        binding.topRatedList.adapter = topRatedAdapter
        initRecyclerViewDividers(binding.nowPlayingList)

        viewModel.observeTopRatedMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                topRatedAdapter.submitMediaList(movieList)
                topRatedAdapter.notifyDataSetChanged()

            })
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






}