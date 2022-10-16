package edu.utap.watchlist.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MediaAdapter
import edu.utap.watchlist.MediaCardAdapter
import edu.utap.watchlist.R
import edu.utap.watchlist.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: MediaCardAdapter

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }


    private fun initAdapter() {
        //addListToAdapter()
        //this.adapter = MediaAdapter()
        this.adapter = MediaCardAdapter(viewModel)
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.mainList.layoutManager = LinearLayoutManager(activity)
        binding.mainList.adapter = adapter
        initRecyclerViewDividers(binding.mainList)

        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observeMedia().observe(viewLifecycleOwner,
            Observer { movieList ->
                adapter.submitMediaList(movieList)
                adapter.notifyDataSetChanged()

            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root





        initMainSelector()
        initSubSelector()
        initAdapter()


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
            viewModel.fetchPopularMovies()
        }
        binding.opt2.setOnClickListener {
            binding.moviesTvControl.check(R.id.opt_2)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.opt1.setBackgroundColor(Color.TRANSPARENT)
            viewModel.fetchPopularTV()
        }
        binding.moviesTvControl.check(R.id.opt_1)
        binding.opt1.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        binding.moviesTvControl.setOnCheckedChangeListener { radioGroup, i ->
            binding.moviesTvControl.check(i)
        }
    }


    private fun initSubSelector() {

    }



}