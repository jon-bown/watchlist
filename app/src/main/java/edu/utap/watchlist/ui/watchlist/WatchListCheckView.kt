package edu.utap.watchlist.ui.watchlist

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.StringListAdapter
import edu.utap.watchlist.databinding.FragmentWatchListCheckViewBinding
import edu.utap.watchlist.ui.profile.SelectionListArgs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WatchListCheckView.newInstance] factory method to
 * create an instance of this fragment.
 */
class WatchListCheckView : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentWatchListCheckViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StringListAdapter

    private val args: SelectionListArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()

    private val listsToAdd = mutableListOf<String>()

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        _binding = FragmentWatchListCheckViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.adapter = StringListAdapter(::toggleLists, true)

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.selectionList.layoutManager = LinearLayoutManager(activity)

        binding.selectionList.layoutManager = manager
        binding.selectionList.adapter = adapter
        initRecyclerViewDividers(binding.selectionList)


        viewModel.observeWatchLists().observe(viewLifecycleOwner) { lists ->

                if(!lists.isEmpty()){
                    binding.watchlistEditDoneButton.isEnabled = true
                    binding.noListText.text = "Select Watchlists"
                }
                //Need all lists where this current item belongs
                adapter.submitList(lists.map{ it.name}, viewModel.getWatchlistNamesThatContain())
                adapter.notifyDataSetChanged()

        }




        binding.watchlistEditDoneButton.setOnClickListener {
            //save items
            collectSelectedLists()


            //Show Toast


            popBackToFragment()
            val act = activity as MainActivity
            act.showNavBar()

        }

        if(viewModel.observeWatchLists().value == null || viewModel.observeWatchLists().value!!.isEmpty()){
            binding.watchlistEditDoneButton.isEnabled = false
            binding.noListText.text  = "You don't have any watchlists!"
        }

        binding.watchlistCancelButton.setOnClickListener {

            popBackToFragment()


            val act = activity as MainActivity
            act.showNavBar()
        }

        val bottomNav = view?.findViewById<BottomNavigationView>(R.id.nav_host_fragment_activity_main)
        bottomNav?.visibility = View.INVISIBLE
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //hide bottom navigation view

        return root
    }

    private fun collectSelectedLists() {
        for(list in listsToAdd) {
            viewModel.addToWatchList(list)
        }
        viewModel.removeFromLists(listsToAdd)

    }

    private fun toggleLists(items: List<String>, selection: String) {
        listsToAdd.clear()
        listsToAdd.addAll(items)
    }

    private fun popBackToFragment() {
        val manager: FragmentManager? = parentFragmentManager
        val backStackId = manager?.getBackStackEntryAt(0)!!.getId();
        manager.popBackStack(backStackId,
            FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WatchListCheckView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            WatchListCheckView().apply {
            }
    }
}