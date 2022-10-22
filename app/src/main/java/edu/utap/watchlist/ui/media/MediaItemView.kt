package edu.utap.watchlist.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import edu.utap.firebaseauth.MainViewModel
import edu.utap.watchlist.R
import edu.utap.watchlist.adapters.MediaCardAdapter
import edu.utap.watchlist.databinding.ActivityMediaItemViewBinding

class MediaItemView : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMediaItemViewBinding
    private val viewModel: MediaItemViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_item_view)

        binding = ActivityMediaItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val type = intent.getStringExtra(MediaCardAdapter.TYPE)
        val id = intent.getStringExtra(MediaCardAdapter.ID)
        val lang = intent.getStringExtra(MediaCardAdapter.LANG)
        val country = intent.getStringExtra(MediaCardAdapter.COUNTRY)
        Log.d("TYPE PASSED", type!!)
        Log.d("ID PASSED", id!!)
        Log.d("LANG PASSED", lang!!)
        Log.d("COUNTRY PASSED", country!!)
        viewModel.setUpData(type, id, lang, country)

        if(type == "Movie"){
            viewModel.observeCurrentMovie().observe(this) {
                binding.movieTitleText.text = it.title
                Log.d("BACKDROP_PATH", it.backdropPath)
                viewModel.netFetchImage(binding.backdrop, it.backdropPath)
                //set other properties
            }
        }
        else {
            viewModel.observeCurrentTV().observe(this) {
                binding.movieTitleText.text = it.title
                Log.d("BACKDROP_PATH", it.backdropPath)
                viewModel.netFetchImage(binding.backdrop, it.backdropPath)
                //set other properties
            }
        }



    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}