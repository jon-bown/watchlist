package edu.utap.watchlist.ui.search

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.adapters.MediaAdapter
import edu.utap.watchlist.R
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentNotificationsBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: MediaAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view



    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    fun openMediaView(item: MediaItem) {
        viewModel.setUpCurrentMediaData(item)
        findNavController().navigate(R.id.navigation_media)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        this.adapter = MediaAdapter(::openMediaView)

        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.searchResults.layoutManager = LinearLayoutManager(activity)

        //val manager = StaggeredGridLayoutManager(-1,StaggeredGridLayoutManager.HORIZONTAL)
        //manager.orientation =
        binding.searchResults.layoutManager = manager
        binding.searchResults.adapter = adapter
        initRecyclerViewDividers(binding.searchResults)

        // Live data lets us display the latest list, whatever it is
        // NB: owner is viewLifecycleOwner
        viewModel.observeMedia().observe(viewLifecycleOwner,
            Observer { movieList ->
                adapter.submitMediaList(movieList)
                adapter.notifyDataSetChanged()

            })





        //setup search
        binding.mediaSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("HELLO","HELLO SEARCH")


                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(newText!! != ""){
                        viewModel.fetchSearchResults(newText!!)
                        //hide keyboard
                    }
                    else{
                        //make toast
                        //hide keyboard
                        val activity = activity as MainActivity
                        activity.hideKeyboard()
                        binding.mediaSearchView.onActionViewCollapsed()
                        binding.mediaSearchView.isFocusable = false
                        viewModel.clearMediaItems()
                    }
                    return true
                }
            })



        initMainSelector()

        return root
    }



    private fun initMainSelector() {
        binding.searchMovies.setOnClickListener {
            binding.moviesTvSearchControl.check(R.id.opt_1)
            binding.searchTv.setBackgroundColor(Color.TRANSPARENT)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            viewModel.updateMediaType(true)
            adapter.notifyDataSetChanged()

        }
        binding.searchTv.setOnClickListener {
            binding.moviesTvSearchControl.check(R.id.opt_2)
            it.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
            binding.searchMovies.setBackgroundColor(Color.TRANSPARENT)
            viewModel.updateMediaType(false)
            adapter.notifyDataSetChanged()

        }
        binding.moviesTvSearchControl.check(R.id.opt_1)
        binding.searchMovies.setBackgroundColor(binding.root.context.getColor(R.color.button_checked))
        binding.moviesTvSearchControl.setOnCheckedChangeListener { radioGroup, i ->
            binding.moviesTvSearchControl.check(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}