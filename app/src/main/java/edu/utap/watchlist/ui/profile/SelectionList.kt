package edu.utap.watchlist.ui.profile

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
import edu.utap.watchlist.adapters.StringListSelectionAdapter
import edu.utap.watchlist.api.Countries
import edu.utap.watchlist.api.Languages
import edu.utap.watchlist.databinding.FragmentSelectionListBinding

class SelectionList : Fragment() {

    private var _binding: FragmentSelectionListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StringListSelectionAdapter

    private val args: SelectionListArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()



    private fun initRecyclerViewDividers(rv: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL )
        rv.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelectionListBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //pass on click listener
        if(args.type == "country"){
            this.adapter = StringListSelectionAdapter(::onCountrySelectionMadeListener)
        }
        else {
            this.adapter = StringListSelectionAdapter(::onLanguageSelectionMadeListener)
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

    private fun onCountrySelectionMadeListener(selection: String) {

        this.viewModel.changeCountrySetting(Countries.countriesMap[selection]!!)
    }
    private fun onLanguageSelectionMadeListener(selection: String) {
        this.viewModel.changeLanguageSetting(Languages.languageMap[selection]!!)
    }

}