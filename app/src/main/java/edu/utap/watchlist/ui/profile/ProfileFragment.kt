package edu.utap.watchlist.ui.profile

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.utap.firebaseauth.AuthInit
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.api.Countries
import edu.utap.watchlist.api.Languages
import edu.utap.watchlist.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()



    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var countrySpinnerAdapter: ArrayAdapter<String>
    private lateinit var languageSpinnerAdapter: ArrayAdapter<String>

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.onSignInResult(it)
            viewModel.updateUser()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initSpinnerAdapters()

        if(savedInstanceState == null) {
            viewModel.populateUserData()
        }
        // XXX Write me. Set data to display in UI
        viewModel.observeDisplayName().observe(viewLifecycleOwner){
            binding.userName.text = it
        }

        viewModel.observeEmail().observe(viewLifecycleOwner){
            binding.userEmail.text = it
        }

        viewModel.observeCountrySetting().observe(viewLifecycleOwner){
            //set selected country setting
           val spinnerPosition = countrySpinnerAdapter.getPosition(it)
            binding.countrySpinner.setSelection(spinnerPosition)
        }

        viewModel.observeLanguageSetting().observe(viewLifecycleOwner){ lang ->
            //binding.languageSpinner.selected
            //Set selected langugage setting
        val languageKey = Languages.numbersMap.filter { lang == it.value }.keys.first()
//
            val spinnerPosition = languageSpinnerAdapter.getPosition(languageKey)
           binding.languageSpinner.setSelection(spinnerPosition)

        }

        viewModel.observeAdultMode().observe(viewLifecycleOwner){
            binding.adultSelector.isChecked = it
        }

        binding.logoutBut.setOnClickListener {
            // XXX Write me.
            viewModel.signOut()
            AuthInit(viewModel, signInLauncher)
        }

        binding.setDisplayName.setOnClickListener {
            // XXX Write me.
            showDialog()
            //
        }

        binding.adultSelector.isChecked = viewModel.observeAdultMode().value as Boolean

        binding.adultSelector.setOnCheckedChangeListener { compoundButton, b ->
            Log.d("Change Adult", b.toString())
            viewModel.changeAdultMode(b)
        }

        initLanguageSpinner()
        initCountrySpinner()

        return root
    }


    fun showDialog(){

        val input = EditText(binding.root.context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Username")
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
        title.text = "Change Username"
        title.textSize = 20F
        title.maxLines = 1
        input.inputType = InputType.TYPE_CLASS_TEXT


        MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
            //.setCustomTitle(title)
            .setView(input)
            .setTitle("Change Username")
            .setPositiveButton("Change", DialogInterface.OnClickListener { dialog, which ->
                // Here you get get input text from the Edittext
                var m_Text = input.text.toString()
                AuthInit.setDisplayName(m_Text, viewModel)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel() })
            .create().show()

    }

    fun initSpinnerAdapters() {
        languageSpinnerAdapter = ArrayAdapter<String>(
            binding.root.context,
            android.R.layout.simple_spinner_dropdown_item, Languages.numbersMap.keys.toTypedArray())

        countrySpinnerAdapter = ArrayAdapter<String>(
            binding.root.context,
            android.R.layout.simple_spinner_dropdown_item, Countries.countriesMap.keys.toTypedArray())

    }


    private fun initLanguageSpinner(){


        binding.languageSpinner.adapter = languageSpinnerAdapter

        Log.d("SETTING", viewModel.observeLanguageSetting().value.toString())

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("Selected Language", position.toString())
            }

        }
    }

    private fun initCountrySpinner() {


//        restaurantTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = countrySpinnerAdapter



        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("Selected Country", position.toString())
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}