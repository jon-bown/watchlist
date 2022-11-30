package edu.utap.watchlist.ui.watchlist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.WatchListItemAdapter
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.components.SwipeToDeleteCallback
import edu.utap.watchlist.databinding.FragmentSingleWatchListViewBinding
import edu.utap.watchlist.ui.media.MediaItemViewFragment

class SingleWatchListView : Fragment() {

    private var _binding: FragmentSingleWatchListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WatchListItemAdapter


    private val viewModel: MainViewModel by activityViewModels()

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentSingleWatchListViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.adapter = WatchListItemAdapter(viewModel, ::openMediaView)

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.watchList.layoutManager = manager
        binding.watchList.adapter = adapter
        initRecyclerViewDividers(binding.watchList)



        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeAt(position)

                Snackbar.make(
                    activity!!.findViewById(R.id.nav_host_fragment_activity_main),
                    "Removed From ${viewModel.observeCurrentWatchList().value!!.name}",
                    Snackbar.LENGTH_SHORT
                ).show()

            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.watchList)



        viewModel.observeCurrentWatchList().observe(viewLifecycleOwner) {

            //Need all lists where this current item belongs
            adapter.submitMediaList(it.items!!.toList())

        }


        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.top_filter, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_seen -> {
                        // clearCompletedTasks()

                        viewModel.setWatchListOnlySeen()
                        true
                    }
                    R.id.action_not_seen -> {

                        viewModel.setWatchListOnlyNotSeen()
                        true
                    }
                    R.id.action_all -> {

                        viewModel.restoreWatchList()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



    fun openMediaView(item: MediaItem) {
        //open single view with given media item
        viewModel.setUpCurrentMediaData(item)
        //view?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.INVISIBLE


        val manager: FragmentManager? = parentFragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, MediaItemViewFragment.newInstance("one"), null)
        transaction.addToBackStack(null)
        transaction.commit()

        val act = activity as MainActivity
        act.hideNavBar()
        act.hideActionBar()

    }


    private fun collectSelectedLists(item: MediaItem) {
        //open single media item view
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SingleWatchListView().apply {
                arguments = Bundle().apply {
                }
            }
    }
}