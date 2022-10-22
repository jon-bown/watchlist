package edu.utap.watchlist.ui.dashboard

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.adapters.MediaAdapter
import edu.utap.watchlist.R
import edu.utap.watchlist.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var watchListAdapter: MediaAdapter

    private fun initAdapter() {
        //addListToAdapter()
        //this.adapter = MediaAdapter()
        this.watchListAdapter = MediaAdapter()
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
                var m_Text = input.text.toString()
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

        viewModel.observeNowPlayingMediaItems().observe(viewLifecycleOwner,
            Observer { movieList ->
                watchListAdapter.submitMediaList(movieList)
                watchListAdapter.notifyDataSetChanged()

            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}