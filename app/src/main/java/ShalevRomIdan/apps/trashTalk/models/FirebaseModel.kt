package trashTalk.apps.trashTalk.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.ktx.Firebase

class FirebaseModel {
    private val db = Firebase.firestore

    companion object {
        const val TRASHES_COLLECTION_PATH = "Trashes"
        const val USERS_COLLECTION_PATH = "Users"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {

            })
            setLocalCacheSettings(persistentCacheSettings {  })
        }
        db.firestoreSettings = settings
    }

    fun getAllTrashes(since:Long, callback: (List<Trash>) -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Trash.LAST_UPDATED, Timestamp(since,0))
            .get()
            .addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val trashes: MutableList<Trash> = mutableListOf()
                    for (json in it.result) {
                        trashes.add(Trash.fromJSON(json.data))
                    }
                    callback(trashes)
                } false -> callback(listOf())
            }
        }
    }

    fun getAllUserTrashes(userEmail:String,since:Long, callback: (List<Trash>) -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Trash.LAST_UPDATED, Timestamp(since,0))
            .whereEqualTo("author", userEmail)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val trashes: MutableList<Trash> = mutableListOf()
                        for (json in it.result) {
                            trashes.add(Trash.fromJSON(json.data))
                        }
                        callback(trashes)
                    } false -> callback(listOf())
                }
            }
    }

    fun deleteTrash(trash: Trash, callback: () -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .document(trash.id)
            .delete()
            .addOnSuccessListener { callback() }
            .addOnFailureListener{
                Log.e("delete trash", "blah", it)
            }
    }

    fun addTrash(trash: Trash, callback: () -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .document(trash.id)
            .set(trash.json)
            .addOnSuccessListener { callback() }
            .addOnFailureListener{
                Log.e("add trash", "blah", it)
            }
    }

    fun editTrash(trash: Trash, callback: () -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .document(trash.id)
            .update(trash.json)
            .addOnSuccessListener { callback() }
            .addOnFailureListener{
                Log.e("update trash", "blah", it)
            }
    }

    fun addUser(user: User, callback: () -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .document(user.id)
            .set(user.json)
            .addOnSuccessListener { callback() }
            .addOnFailureListener{
                Log.e("add user", "blah", it)
            }
    }

    fun updateUser(id:String, nickname:String, imageUri:String, callback: () -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .document(id)
            .update(mapOf(
                "nickname" to nickname,
                "imageUrl" to imageUri
            ))
            .addOnCompleteListener {
                Log.i("updateUser", "success")
                callback()
            }
            .addOnFailureListener{
                Log.e("updateUser", "error")
                callback()
            }
    }

    fun getUserByEmail(email:String, callback: (User) -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && !it.result.isEmpty) {
                    callback(User.fromJSON(it.result.first().data))
                }
            }.addOnFailureListener{
                Log.e("getUser", "blah", it)
            }
    }

    fun getTrashById(id:String, callback: (Trash) -> Unit) {
        db.collection(TRASHES_COLLECTION_PATH)
            .whereEqualTo("id", id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && !it.result.isEmpty) {
                    callback(Trash.fromJSON(it.result.first().data))
                }
            }.addOnFailureListener{
                Log.e("getTrash", "blah", it)
            }
    }

}