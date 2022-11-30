package edu.utap.watchlist.ui.search

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import edu.utap.watchlist.adapters.MediaAdapter
import edu.utap.watchlist.R
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentSearchBinding
import edu.utap.watchlist.ui.media.MediaItemViewFragment

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: MediaAdapter

    private var isLoading = false

    private val binding get() = _binding!!
    private var currentSearchPage = 1
    private var queryText: String = ""

    private fun setLoadingListener() {
        viewModel.observeFetchDone().observe(viewLifecycleOwner) {
            isLoading = false
        }
    }

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root


        this.adapter = MediaAdapter(::openMediaView)

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.searchResults.layoutManager = LinearLayoutManager(activity)

        binding.searchResults.layoutManager = manager
        binding.searchResults.adapter = adapter
        initRecyclerViewDividers(binding.searchResults)



        viewModel.observeMedia().observe(viewLifecycleOwner,
            Observer { movieList ->
                adapter.submitMediaList(movieList)
            })



        //setup search

        binding.mediaSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query == ""){
                        queryText = ""
                        viewModel.clearMediaItems()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(newText != ""){
                        queryText = newText
                        viewModel.fetchSearchResults(newText, 1)
                        //hide keyboard
                    }
                    else{
                        //make toast
                        //hide keyboard
                        //val activity = activity as MainActivity
                        //activity.hideKeyboard()
                        //binding.mediaSearchView.isFocusable = false
                        queryText = ""
                        viewModel.clearMediaItems()
                    }
                    return true
                }
            })

        viewModel.observeSearchMovieMode().observe(viewLifecycleOwner,
            Observer { movie ->
                if(movie){
                    binding.searchMovies.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
                }
                else {
                    binding.searchTv.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
                }
            })

        setLoadingListener()
        initMainSelector()
        initSearchListScrollListener()

        return root
    }



    private fun initMainSelector() {
        binding.searchMovies.setOnClickListener {
            viewModel.updateSearchMovieMode(true)
            binding.searchTv.setBackgroundColor(Color.TRANSPARENT)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            if(queryText != ""){
                viewModel.fetchSearchResults(queryText!!, 1)
            }
            else{
                viewModel.clearMediaItems()
            }
            binding.moviesTvSearchControl.check(R.id.opt_1)

        }
        binding.searchTv.setOnClickListener {
            viewModel.updateSearchMovieMode(false)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.searchMovies.setBackgroundColor(Color.TRANSPARENT)
            if(queryText != ""){
                viewModel.fetchSearchResults(queryText!!, 1)
            }
            else{
                viewModel.clearMediaItems()
            }
            binding.moviesTvSearchControl.check(R.id.opt_2)
        }
    }



    private fun initSearchListScrollListener() {
        binding.searchResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                //binding.mediaSearchView.setVisibility(View.INVISIBLE);
                //binding.mediaSearchView.setVisibility(View.VISIBLE);
                if (!isLoading) {
                    if(currentSearchPage < 20) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                            (viewModel.observeMedia().value!!.size - 10)) {
                            isLoading = true
                            currentSearchPage+=1
                            if(queryText != ""){
                                viewModel.fetchSearchResults(queryText, currentSearchPage)
                            }

                        }
                    }

                }
            }
        })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}