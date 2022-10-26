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
import java.util.*

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
            email = user!!.email!!
            displayName = user!!.displayName!!
        }

    }

    fun getEmail(): String {
        return email
    }
    fun getUserName(): String {
        return displayName
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
            createDocument()
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
            createDocument()
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
            createDocument()
        }
        return adultMode
    }


    suspend fun getWatchLists(): List<WatchList> {
        Log.d("ADULT MODE", "1")
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document("dM2OT8ycb53ZqDTo6Lvd")
        val document = docRef.get().await()
        if (document != null) {
            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            userLists = document.data!!.get("lists") as List<WatchList>
        } else {
            Log.d(TAG, "No such document")
            createDocument()

        }
        return userLists
    }





    fun setAdultMode(mode: Boolean) {
        db.collection("users")
            .document("dM2OT8ycb53ZqDTo6Lvd")
            .update(mapOf(
                "adult" to mode
        ))
    }

    fun setLanguage(language: String) {
        db.collection("users")
            .document("dM2OT8ycb53ZqDTo6Lvd")
            .update(mapOf(
                "language" to language
            ))
    }

    fun setCountry(country: String) {
        db.collection("users")
            .document("dM2OT8ycb53ZqDTo6Lvd")
            .update(mapOf(
                "country" to country
            ))
    }

    private fun createDocument() {
        val newUser = hashMapOf(
            "adult" to false,
            "language" to Locale.getDefault().getLanguage() ,
            "country" to "US"
        )

        db.collection("users").document(user!!.uid)
            .set(newUser)
            .addOnSuccessListener { Log.d("", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("", "Error writing document", e) }
    }


}