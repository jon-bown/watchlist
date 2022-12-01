package edu.utap.watchlist.ui.watchlist

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.StringListAdapter
import edu.utap.watchlist.databinding.FragmentWatchListCheckViewBinding
import edu.utap.watchlist.ui.profile.SelectionListArgs


class WatchListCheckView : Fragment() {

    private var _binding: FragmentWatchListCheckViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StringListAdapter

    private val viewModel: MainViewModel by activityViewModels()

    private val listsToAdd = mutableListOf<String>()

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWatchListCheckViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.adapter = StringListAdapter(::toggleLists, true)
        this.adapter.submitList(viewModel.observeWatchLists().value!!.map { it.name!! }, viewModel.getWatchlistNamesThatContain())
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.selectionList.layoutManager = manager
        binding.selectionList.adapter = adapter
        initRecyclerViewDividers(binding.selectionList)


        viewModel.observeWatchLists().observe(viewLifecycleOwner) { lists ->
                if(!lists.isEmpty()){
                    binding.watchlistEditDoneButton.isEnabled = true
                    binding.noListText.text = "Select Watchlists"
                }
                val newList = lists.sortedBy { it.name }.map{ it.name!!}
                //Need all lists where this current item belongs
                adapter.submitList(newList, viewModel.getWatchlistNamesThatContain())
        }


        binding.watchlistEditDoneButton.setOnClickListener {
            collectSelectedLists()

            popBackToFragment()
            val act = activity as MainActivity


            Snackbar.make(
                act.findViewById(R.id.nav_host_fragment_activity_main),
                "Added To Your Lists!",
                1000
            ).show()

        }

        if(viewModel.observeWatchLists().value == null || viewModel.observeWatchLists().value!!.isEmpty()){
            binding.watchlistEditDoneButton.isEnabled = false
            binding.noListText.text  = "You don't have any watchlists!"
        }

        binding.watchlistCancelButton.setOnClickListener {
            popBackToFragment()
        }


        val bottomNav = view?.findViewById<BottomNavigationView>(R.id.nav_host_fragment_activity_main)
        bottomNav?.visibility = View.INVISIBLE
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return root
    }

    private fun collectSelectedLists() {
        for(list in viewModel.observeWatchLists().value!!){
            if(list.name in listsToAdd){
                viewModel.addToWatchList(list.name!!)
            }
            else {
                viewModel.removeFromWatchList(list.name!!)
            }

        }
    }

    private fun toggleLists(items: List<String>, selection: String) {
        listsToAdd.clear()
        listsToAdd.addAll(items)
    }

    private fun popBackToFragment() {
        val manager: FragmentManager? = parentFragmentManager
        manager!!.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WatchListCheckView().apply {
            }
    }
}