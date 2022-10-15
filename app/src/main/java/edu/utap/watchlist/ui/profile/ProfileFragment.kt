package edu.utap.watchlist.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import edu.utap.firebaseauth.AuthInit
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        if(savedInstanceState == null) {
            binding.displayNameET.text.clear()
        }
        // XXX Write me. Set data to display in UI
        viewModel.observeDisplayName().observe(this){
            binding.userName.text = it
        }

        viewModel.observeEmail().observe(this){
            binding.userEmail.text = it
        }

        viewModel.observeUid().observe(this){
            binding.userUid.text = it
        }

        binding.logoutBut.setOnClickListener {
            // XXX Write me.
            viewModel.signOut()
        }
        binding.loginBut.setOnClickListener {
            // XXX Write me.
            AuthInit(viewModel, signInLauncher)
        }
        binding.setDisplayName.setOnClickListener {
            // XXX Write me.
            AuthInit.setDisplayName(binding.displayNameET.text.toString(), viewModel)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}