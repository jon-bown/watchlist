package edu.utap.watchlist.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.MediaAdapter
import edu.utap.watchlist.adapters.StringListAdapter
import edu.utap.watchlist.api.Countries
import edu.utap.watchlist.api.Languages
import edu.utap.watchlist.databinding.FragmentHomeBinding
import edu.utap.watchlist.databinding.FragmentNotificationsBinding
import edu.utap.watchlist.databinding.FragmentSelectionListBinding
import io.grpc.NameResolver.Args

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectionList.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectionList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    private var _binding: FragmentSelectionListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StringListAdapter

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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSelectionListBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //pass on click listener
        if(args.type == "country"){
            this.adapter = StringListAdapter(::onCountrySelectionMadeListener)
        }
        else {
            this.adapter = StringListAdapter(::onLanguageSelectionMadeListener)
        }




        //Linear
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.selectionList.layoutManager = LinearLayoutManager(activity)

        binding.selectionList.layoutManager = manager
        binding.selectionList.adapter = adapter
        initRecyclerViewDividers(binding.selectionList)

        if(args.type == "country"){
            viewModel.observeCountrySetting().observe(viewLifecycleOwner) { country ->
                val countryKey = Countries.countriesMap.filter { country == it.value }.keys
                if(countryKey.isNotEmpty()){
                    adapter.submitList(Countries.countriesMap.keys.toMutableList(), countryKey.first())
                    adapter.notifyDataSetChanged()
                }
            }
        }
        else {
            viewModel.observeLanguageSetting().observe(viewLifecycleOwner) { lang ->
                val languageKey = Languages.languageMap.filter { lang == it.value }.keys
                if(languageKey.isNotEmpty()){
                    adapter.submitList(Languages.languageMap.keys.toMutableList(), languageKey.first())
                    adapter.notifyDataSetChanged()
                }
            }
        }

        return root
    }


    //set on click listener for the adapter

    private fun onCountrySelectionMadeListener(selection: String) {

        this.viewModel.changeCountrySetting(Countries.countriesMap[selection]!!)
    }
    private fun onLanguageSelectionMadeListener(selection: String) {
        this.viewModel.changeLanguageSetting(Languages.languageMap[selection]!!)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectionList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            SelectionList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}