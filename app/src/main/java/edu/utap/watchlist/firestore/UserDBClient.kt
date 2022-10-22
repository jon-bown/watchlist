package edu.utap.watchlist.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.utap.watchlist.api.WatchList

class UserDBClient {

    private val db = Firebase.firestore
    private var docName = ""
    private var country = ""
    private var language = ""
    private var adultMode = false
    private var userLists = emptyList<WatchList>()
    var user = FirebaseAuth.getInstance().currentUser


    init {
        if(user != null){
            docName = user!!.uid
        }

        initializeUser()
    }

    fun initializeUser() {
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document("dM2OT8ycb53ZqDTo6Lvd")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    country = document.data!!.get("country").toString()
                    language = document.data!!.get("language").toString()
                    adultMode = document.data!!.get("adult") as Boolean

                    //handle the case when these are defaults

                } else {
                    Log.d(TAG, "No such document")

                    //create document with docname

                    //set defaults
                    adultMode = false
                    language = "en"
                    country = "US"
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    //Update separate settings

    fun getCountry(): String {
        return country
    }

    fun getLanguage(): String {
        return language
    }

    fun getAdultMode(): Boolean {
        return adultMode
    }


}