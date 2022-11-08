package edu.utap.watchlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.utap.firebaseauth.AuthInit
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    //private lateinit var binding : ActivityMainBinding

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.onSignInResult(it)
            viewModel.updateUser()
        }

    private lateinit var binding: ActivityMainBinding


    fun hideKeyboard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.root.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                hideKeyboard()
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> null //Do Something
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        
        AuthInit(viewModel, signInLauncher)


    }

    fun hideNavBar() {
        binding.navView.visibility = View.GONE
        supportActionBar!!.hide()
//        groupDetails.visibility = View.GONE
    }

    fun showNavBar() {
        binding.navView.visibility = View.VISIBLE
        supportActionBar!!.show()
//        groupDetails.visibility = View.GONE
    }

    fun openBrowser(view: View) {
        //Get url from tag
        val url = view.getTag() as String
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        //pass the url to intent data
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            //findNavController().popBackStack()
            findNavController(R.id.nav_host_fragment_activity_main).popBackStack()
            //if backstack is zero
            findViewById<ImageView>(R.id.backdrop)?.setImageDrawable(null)

        }
        return true
    }


}