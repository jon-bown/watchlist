package edu.utap.watchlist.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MediaCardAdapter
import edu.utap.watchlist.R
import edu.utap.watchlist.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private var listDisplayType: String = "popular"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var popularAdapter: MediaCardAdapter
    private lateinit var nowPlayingAdapter: MediaCardAdapter
    private lateinit var topRatedAdapter: MediaCardAdapter


    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }


    private fun initAdapter() {
        //addListToAdapter()
        //this.adapter = MediaAdapter()
        this.popularAdapter = MediaCardAdapter(viewModel)
        this.nowPlayingAdapter = MediaCardAdapter(viewModel)
        this.topRatedAdapter = MediaCardAdapter(viewModel)

    }

    private fun notifiyAdaptersChanged() {
        popularAdapter.notifyDataSetChanged()
        nowPlayingAdapter.notifyDataSetChanged()
        topRatedAdapter.notifyDataSetChanged()
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


        initMainSelector()
        initSubSelector()

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


    private fun initSubSelector() {
        binding.popular.setOnClickListener {
            binding.listType.check(R.id.popular)
            listDisplayType = "popular"
            resetSelectorBackgroundColor()
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            viewModel.netRefresh()
            notifiyAdaptersChanged()
        }
        binding.nowPlaying.setOnClickListener {
            binding.listType.check(R.id.nowPlaying)
            listDisplayType = "nowPlaying"
            resetSelectorBackgroundColor()
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            viewModel.netRefresh()
            notifiyAdaptersChanged()
        }
        binding.topRated.setOnClickListener {
            binding.listType.check(R.id.topRated)
            listDisplayType = "topRated"
            resetSelectorBackgroundColor()
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            viewModel.netRefresh()
            notifiyAdaptersChanged()
        }
        binding.listType.check(R.id.popular)
        binding.popular.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        binding.listType.setOnCheckedChangeListener { radioGroup, i ->
            binding.listType.check(i)
        }



    }


    private fun resetSelectorBackgroundColor(){
        binding.nowPlaying.setBackgroundColor(Color.TRANSPARENT)
        binding.popular.setBackgroundColor(Color.TRANSPARENT)
        binding.topRated.setBackgroundColor(Color.TRANSPARENT)

        //update current selection
        if(listDisplayType == "nowPlaying"){
            binding.nowPlaying.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        }
        else if(listDisplayType == "popular"){
            binding.popular.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        }
        else if(listDisplayType == "topRated"){
            binding.topRated.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        }
    }
}