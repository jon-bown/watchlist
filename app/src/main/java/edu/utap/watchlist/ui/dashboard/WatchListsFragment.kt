package edu.utap.watchlist.ui.dashboard

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.WatchListAdapter
import edu.utap.watchlist.components.SwipeToDeleteCallback
import edu.utap.watchlist.databinding.FragmentDashboardBinding


class WatchListsFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var watchListAdapter: WatchListAdapter

    private fun initAdapter() {
        //addListToAdapter()
        //this.adapter = MediaAdapter()
        this.watchListAdapter = WatchListAdapter(viewModel, ::openWatchList)
    }


    private fun openWatchList(selection: String) {
        //set current list in viewModel
        viewModel.setCurrentWatchList(selection)
        findNavController().navigate(
            WatchListsFragmentDirections.actionWatchlistsToWatchlist(),
            NavOptions.Builder().setLaunchSingleTop(true).build())

        val act = activity as MainActivity
        act.hideNavBar()
        act.setActionBarTitle(selection)

    }


    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root



        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
        initAdapter()
        initWatchLists()

        return root
    }

    //https://handyopinion.com/show-alert-dialog-with-an-input-field-edittext-in-android-kotlin/
    fun showDialog(){

        val input = EditText(binding.root.context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Enter List Name")
        input.inputType = InputType.TYPE_CLASS_TEXT

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

//        val param = input.layoutParams as ViewGroup.MarginLayoutParams
//        param.setMargins(10,3,5,3)
//        input.layoutParams = param
//        //input.setPadding(6)

        val title = TextView(binding.root.context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        title.text = "New List"
        title.textSize = 20F
        title.maxLines = 1
        input.inputType = InputType.TYPE_CLASS_TEXT


        MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
            //.setCustomTitle(title)
            .setView(input)
            .setTitle("New List")
            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                // Here you get get input text from the Edittext
                var listName = input.text.toString()
                //save watchlist
                viewModel.addNewWatchList(listName)

            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel() })
            .create().show()

    }


    private fun initWatchLists() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL

        binding.myWatchLists.layoutManager = manager
        binding.myWatchLists.adapter = watchListAdapter
        initRecyclerViewDividers(binding.myWatchLists)


        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition



                watchListAdapter.removeAt(position)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.myWatchLists)


        viewModel.observeWatchLists().observe(viewLifecycleOwner,
            Observer { watchList ->
                val listNames = watchList.map{
                    it.name!!
                }
                watchListAdapter.submitWatchListNames(listNames)
                watchListAdapter.notifyDataSetChanged()

            })

    }

    //passed to adapter
    private fun onWatchListClickListener() {
        //open singlewatchlistview fragment
        TODO("Open single watchlist view")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}