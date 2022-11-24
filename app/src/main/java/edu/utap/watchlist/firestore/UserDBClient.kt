package edu.utap.watchlist.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.api.User
import edu.utap.watchlist.api.WatchList
import kotlinx.coroutines.tasks.await
import java.util.*

class UserDBClient {

    private val db = Firebase.firestore
    private var docName = ""
    private var country = ""
    private var language = ""
    private var adultMode = false
    private var userLists = emptyList<WatchList>()
    private var seenList = emptyList<String>()
    var user = FirebaseAuth.getInstance().currentUser
    private var displayName = ""
    private var email = ""
    private var uid = ""
    private var userData = User()


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


    suspend fun getUserData(): User {
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document(user!!.uid)
        val document = docRef.get().await()
        if(document != null){
            userData = document!!.toObject(User::class.java)!!


        }
        else {
            createDocument()
        }
        return userData
    }


    suspend fun getLanguage(): String {
        Log.d("GET LANGUAGE", "1")
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document(user!!.uid)
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
        val docRef = db.collection("users").document(user!!.uid)
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
        val docRef = db.collection("users").document(user!!.uid)
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
        val TAG = "INIT_USER"
        val docRef = db.collection("users").document(user!!.uid)
        val document = docRef.get().await()
        if(document != null){
            try {
                val item = document!!.toObject(User::class.java)
                Log.d(TAG, "DocumentSnapshot data: ${item}")
                var watchLists = mutableListOf<WatchList>()
                for(list in item!!.lists!!.keys){
                    watchLists.add(WatchList(list, item!!.lists!!.get(list)!!.toMutableList()))
                }
                userLists = watchLists.toList()
            }
            catch (e: Exception) {
                userLists = emptyList()
                return userLists
            }


        }
        else {
            createDocument()
        }
        return userLists
    }

    fun removeWatchList(list: WatchList){
        db.collection("users").document(user!!.uid)
            .update(FieldPath.of("lists", list.name), FieldValue.delete())
    }

    fun addMediaItemToWatchlist(name: String, item: MediaItem) {
        db.collection("users").document(user!!.uid).update(
            FieldPath.of("lists", name), FieldValue.arrayUnion(item)
        )
    }

    fun removeMediaItemFromWatchlist(name: String, item: MediaItem) {
        db.collection("users").document(user!!.uid).update(
            FieldPath.of("lists", name), FieldValue.arrayRemove(item)
        )
    }


    fun addWatchList(list: WatchList){

        val update = mapOf(
            list.name to list.items
        )


        db.collection("users").document(user!!.uid)
            .update("lists", update)
    }


    suspend fun getSeenMediaItems(): List<String> {
        val docRef = db.collection("users").document(user!!.uid)
        val document = docRef.get().await()
        if(document != null){
            val list = document.data!!.get("seen")
            if(list == null) {
                seenList == emptyList<String>()
            }
            else {
                seenList = list as List<String>
            }
        }
        else {
            createDocument()
        }
        return seenList
    }


    fun addSeenItem(item: String){
        db.collection("users").document(user!!.uid)
        .update("seen", FieldValue.arrayUnion(item))

    }

    fun removeSeenItem(item: String){
        db.collection("users").document(user!!.uid)
            .update("seen", FieldValue.arrayRemove(item))
    }





    fun setAdultMode(mode: Boolean) {
        db.collection("users")
            .document(user!!.uid)
            .update(mapOf(
                "adult" to mode
        ))
    }

    fun setLanguage(language: String) {
        db.collection("users")
            .document(user!!.uid)
            .update(mapOf(
                "language" to language
            ))
    }

    fun setCountry(country: String) {
        db.collection("users")
            .document(user!!.uid)
            .update(mapOf(
                "country" to country
            ))
    }


    //Creates default document for new user
    private fun createDocument() {
        val newUser = hashMapOf(
            "adult" to false,
            "language" to Locale.getDefault().getLanguage() ,
            "country" to "US",
            "seen" to emptyList<String>(),
            "lists" to emptyMap<String, List<MediaItem>>()
        )

        db.collection("users").document(user!!.uid)
            .set(newUser)
            .addOnSuccessListener { Log.d("", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("", "Error writing document", e) }
    }


}