package edu.utap.watchlist.ui.watchlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.MediaWatchListItemAdapter
import edu.utap.watchlist.adapters.StringListAdapter
import edu.utap.watchlist.adapters.WatchListAdapter
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.databinding.FragmentSingleWatchListViewBinding
import edu.utap.watchlist.databinding.FragmentWatchListCheckViewBinding
import edu.utap.watchlist.ui.profile.SelectionListArgs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SingleWatchListView.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingleWatchListView : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSingleWatchListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MediaWatchListItemAdapter

    private val args: SelectionListArgs by navArgs()

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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentSingleWatchListViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.adapter = MediaWatchListItemAdapter(::collectSelectedLists)

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.watchList.layoutManager = LinearLayoutManager(activity)

        binding.watchList.layoutManager = manager
        binding.watchList.adapter = adapter
        initRecyclerViewDividers(binding.watchList)


        viewModel.observeCurrentWatchList().observe(viewLifecycleOwner) {

            //Need all lists where this current item belongs
            adapter.submitMediaList(it.items!!.toList())
            adapter.notifyDataSetChanged()

        }


        return root
    }

    private fun collectSelectedLists(item: MediaItem) {
        //open single media item view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SingleWatchListView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SingleWatchListView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}