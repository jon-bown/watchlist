package edu.utap.watchlist.firestore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.utap.watchlist.api.MediaItem
import edu.utap.watchlist.api.User
import edu.utap.watchlist.api.WatchList
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.*

class UserDBClient {

    companion object {
        private const val TABLE = "users"
        private const val WATCHLIST_FIELD = "lists"
    }

    private val db = Firebase.firestore
    private var docName = ""
    private var country = ""
    private var language = ""
    private var adultMode = false
    private var userLists = emptyList<WatchList>()
    private var seenList = emptyList<String>()
    private var user = FirebaseAuth.getInstance().currentUser
    private var displayName = ""
    private var email = ""
    private var userData = User()


    init {
        user = FirebaseAuth.getInstance().currentUser
    }

    fun getEmail(): String {
        return email
    }
    fun getUserName(): String {
        return displayName
    }


    suspend fun getUserData(): User {
        if(docName != "") {
            val docRef = db.collection(TABLE).document(docName)

            val document = docRef.get().await()
            if(document.data != null){

                userData = document!!.toObject(User::class.java)!!
            }
            else {
                createDocument()
            }
        }
        else {
            createDocument()
        }

        return userData
    }


    suspend fun getLanguage(): String {
        val docRef = db.collection(TABLE).document(docName)
        val document = docRef.get().await()
        if (document != null) {
            language = document.data!!.get("language").toString()
        } else {
            createDocument()
        }
        return language
    }


    //Update separate settings

    suspend fun getCountry(): String {
        val docRef = db.collection(TABLE).document(docName)
        val document = docRef.get().await()
        if (document != null) {
            country = document.data!!.get("country").toString()
        } else {
            createDocument()
        }
        return country
    }


    suspend fun getAdultMode(): Boolean {
        val docRef = db.collection(TABLE).document(docName)
        val document = docRef.get().await()

        if (document != null) {
            adultMode = document.data!!.get("adult") as Boolean
        } else {
            createDocument()
        }
        return adultMode
    }


    suspend fun getWatchLists(): List<WatchList> {
        if(docName != ""){
            val docRef = db.collection(TABLE).document(docName)
            val document = docRef.get().await()
            if(document != null){
                try {
                    val item = document!!.toObject(User::class.java)
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
        else {
            return emptyList()
        }

    }

    fun removeWatchList(list: WatchList){
        db.collection(TABLE).document(docName)
            .update(FieldPath.of(WATCHLIST_FIELD, list.name), FieldValue.delete())
    }

    fun addMediaItemToWatchlist(name: String, item: MediaItem, numItems: Int) {
        val doc = db.collection(TABLE).document(docName)
        if(numItems == 0){
            doc.update(
                FieldPath.of(WATCHLIST_FIELD, name), listOf(item)
            ).addOnSuccessListener {

            }.addOnFailureListener { }
        }
        else {
            doc.update(
                FieldPath.of(WATCHLIST_FIELD, name), FieldValue.arrayUnion(item)
            ).addOnSuccessListener {

            }.addOnFailureListener { }
        }


    }



    fun refreshWatchLists(item: MediaItem, newNames: List<String>, watchLists: List<WatchList>): MutableLiveData<List<WatchList>> {

        val doc = db.collection(TABLE).document(docName)

        var mutableLiveData = MutableLiveData<List<WatchList>>()

        val transaction =  db.runTransaction { transaction ->
            val snapshot = transaction.get(doc)
            for (list in watchLists) {
                if (list.name in newNames) {
                    //add
                    if (list.items!!.size == 0) {
                        transaction.update(
                            doc,
                            FieldPath.of(WATCHLIST_FIELD, list.name), listOf(item)
                        )
                    } else {
                        transaction.update(
                            doc,
                            FieldPath.of(WATCHLIST_FIELD, list.name), FieldValue.arrayUnion(item)
                        )
                    }
                } else {
                    //remove
                    if (item in list.items!!) {

                        transaction.update(
                            doc,
                            FieldPath.of(WATCHLIST_FIELD, list.name), FieldValue.arrayRemove(item)
                        )

                    }
                }

                val item = snapshot.toObject(User::class.java)
                var watchLists = mutableListOf<WatchList>()
                for(list in item!!.lists!!.keys){
                    watchLists.add(WatchList(list, item!!.lists!!.get(list)!!.toMutableList()))
                }
                userLists = watchLists
                mutableLiveData.postValue(watchLists.toList())


            }

        }.addOnCompleteListener { task ->

        }.addOnFailureListener {
        }

        return mutableLiveData


    }

    fun removeMediaItemFromWatchlist(name: String, item: MediaItem) {
        db.collection(TABLE).document(docName).update(
            FieldPath.of(WATCHLIST_FIELD, name), FieldValue.arrayRemove(item)
        )
    }


    fun addWatchList(list: WatchList){

        val update = userLists.toMutableList().map { it.name to it.items }.toMap().toMutableMap()
        update[list.name] = list.items
        db.collection(TABLE).document(user!!.uid)
            .update(WATCHLIST_FIELD, update)
    }


    suspend fun getSeenMediaItems(): List<String> {
        if(docName != "") {
            val docRef = db.collection(TABLE).document(user!!.uid)
            val document = docRef.get().await()
            if (document != null) {
                val list = document.data!!.get("seen")
                if (list == null) {
                    seenList == emptyList<String>()
                } else {
                    seenList = list as List<String>
                }
            } else {
                createDocument()
            }
            return seenList

        }
        else {
            return emptyList()
        }
    }


    fun addSeenItem(item: String){
        db.collection(TABLE).document(user!!.uid)
        .update("seen", FieldValue.arrayUnion(item))

    }

    fun removeSeenItem(item: String){
        db.collection(TABLE).document(docName)
            .update("seen", FieldValue.arrayRemove(item))
    }

    fun setAdultMode(mode: Boolean) {
        db.collection(TABLE)
            .document(docName)
            .update(mapOf(
                "adult" to mode
        ))
    }

    fun setLanguage(language: String) {
        db.collection(TABLE)
            .document(docName)
            .update(mapOf(
                "language" to language
            ))
    }

    fun setCountry(country: String) {
        db.collection(TABLE)
            .document(docName)
            .update(mapOf(
                "country" to country
            ))
    }

    fun populateUser() {
        user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            docName = user!!.uid
            email = user!!.email!!
            displayName = user!!.displayName!!
        }
    }


    //Creates default document for new user
    private fun createDocument() {
        val newUser = hashMapOf(
            "adult" to false,
            "language" to Locale.getDefault().getLanguage() ,
            "country" to "US",
            "seen" to emptyList<String>(),
            WATCHLIST_FIELD to emptyMap<String, List<MediaItem>>()
        )

        populateUser()

        if(user != null){
            db.collection(TABLE).document(docName)
                .set(newUser)
                .addOnSuccessListener { }
                .addOnFailureListener { }
        }


    }


}