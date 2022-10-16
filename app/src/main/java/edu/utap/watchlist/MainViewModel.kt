package edu.utap.firebaseauth

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.watchlist.api.MediaRepository
import edu.utap.watchlist.api.Movie
import edu.utap.watchlist.api.MovieDBApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var displayName = MutableLiveData("Uninitialized")
    private var email = MutableLiveData("Uninitialized")
    private var uid = MutableLiveData("Uninitialized")
    private var user: FirebaseUser? = null


    private val movieApi = MovieDBApi.create()
    private val repository = MediaRepository(movieApi)
    private val movies = MutableLiveData<List<Movie>>()


    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        // XXX one-liner to kick off the app
        netRefresh()
    }




    fun netRefresh() {
        // This is where the network request is initiated.
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            var list = repository.fetchPopularMovies()
            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                movies.postValue(list)
            }
        }
    }


    // Observations

    fun observeFetchDone(): LiveData<Boolean> {
        return fetchDone
    }


    fun observeMovies(): LiveData<List<Movie>> {
        return movies
    }


    private fun userLogout() {
        displayName.postValue("No user")
        email.postValue("No email, no active user")
        uid.postValue("No uid, no active user")
    }

    fun updateUser() {
        // XXX Write me. Update user data in view model
        user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            displayName.postValue("No user")
            email.postValue("No email, no active user")
            uid.postValue("No uid, no active user")
        }
        else {
            displayName.value = user?.displayName
            email.value = user?.email
            uid.value = user?.uid
        }

    }

    fun observeDisplayName() : LiveData<String> {
        return displayName
    }
    fun observeEmail() : LiveData<String> {
        return email
    }
    fun observeUid() : LiveData<String> {
        return uid
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
        user = null
    }


    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d("SIGN IN GOOD", "COULD SIGN IN")
            user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            Log.d("SIGN IN ERROR", "COULDNT SIGN IN")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}