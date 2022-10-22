package edu.utap.watchlist.firestore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.utap.watchlist.api.WatchList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDBClient {

    private val db = Firebase.firestore
    private var docName = ""
    private var country = ""
    private var language = ""
    private var adultMode = false
    private var userLists = emptyList<WatchList>()
    var user = FirebaseAuth.getInstance().currentUser
    private var displayName = ""
    private var email = ""
    private var uid = ""


    init {
        if(user != null){
            docName = user!!.uid
        }

    }

    suspend fun getLanguage(): String {
        Log.d("GET LANGUAGE", "1")
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document("dM2OT8ycb53ZqDTo6Lvd")
        val document = docRef.get().await()
        if (document != null) {
            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            language = document.data!!.get("language").toString()
        } else {
            Log.d(TAG, "No such document")
            language = "en"
        }
        return language
    }


    //Update separate settings

    suspend fun getCountry(): String {
        Log.d("GET COUNTRY", "1")
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document("dM2OT8ycb53ZqDTo6Lvd")
        val document = docRef.get().await()
        if (document != null) {
            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            country = document.data!!.get("country").toString()
        } else {
            Log.d(TAG, "No such document")
            country = "US"
        }
        return country
    }


    suspend fun getAdultMode(): Boolean {
        Log.d("ADULT MODE", "1")
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document("dM2OT8ycb53ZqDTo6Lvd")
        val document = docRef.get().await()
        if (document != null) {
            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            adultMode = document.data!!.get("adult") as Boolean
        } else {
            Log.d(TAG, "No such document")
            adultMode = false
        }
        return adultMode
    }


}