package trashTalk.apps.trashTalk.models

import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import trashTalk.apps.trashTalk.base.MyApplication
import trashTalk.apps.trashTalk.dao.AppLocalDataBase
import java.util.concurrent.Executors
import java.util.stream.Collectors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val database = AppLocalDataBase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel = FirebaseModel()
    private val auth = Firebase.auth
    private val trashes:LiveData<MutableList<Trash>>? = null
    private val myTrashes:LiveData<MutableList<Trash>>? = null

    val trashListLoadingState:MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)

    companion object {
        val instance: Model = Model()
    }

    interface getAllTrashesListener {
        fun onComplete(trashes:List<Trash>)
    }

    fun getAllTrashes():LiveData<MutableList<Trash>> {
        refreshAllTrashes()
        return trashes ?: database.trashDao().getAll()
    }

    fun refreshAllTrashes(){
        trashListLoadingState.value = LoadingState.LOADING

        val lastUpdated:Long =  Trash.lastUpdated
        firebaseModel.getAllTrashes(lastUpdated) {list ->
            executor.execute {
                var time = lastUpdated
                for (trash in list) {
                    database.trashDao().insert(trash)

                    trash.lastUpdated?.let {
                        if (time < it) {
                            time = trash.lastUpdated ?: System.currentTimeMillis()
                        }
                    }
                    Trash.lastUpdated = time
                }
                trashListLoadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun refreshAllUserTrashes(userEmail: String){
        trashListLoadingState.value = LoadingState.LOADING
        val lastUpdated:Long =  Trash.userTrashesLastUpdated
        firebaseModel.getAllUserTrashes(userEmail,lastUpdated) {list ->
            executor.execute {
                var time = lastUpdated
                for (trash in list) {
                    database.trashDao().insert(trash)

                    trash.lastUpdated?.let {
                        if (time < it) {
                            time = trash.lastUpdated ?: System.currentTimeMillis()
                        }
                    }
                    Trash.userTrashesLastUpdated = time
                }
                trashListLoadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun getAllTrashesOfUser(userEmail:String): LiveData<MutableList<Trash>>? {
        refreshAllUserTrashes(userEmail)
        return myTrashes ?: database.trashDao().getTrashesByUser(userEmail)
    }

    fun addTrash(trash: Trash, callback: () -> Unit) {
        firebaseModel.addTrash(trash) {
            refreshAllTrashes()
            callback()
        }
    }

    fun editTrash(trash: Trash, callback: () -> Unit) {
        firebaseModel.editTrash(trash) {
            refreshAllUserTrashes(MyApplication.Globals.user?.email ?: "")
            callback()
        }
    }

    fun deleteTrash(trash: Trash, callback: () -> Unit) {
        firebaseModel.deleteTrash(trash) {
            executor.execute {
                database.trashDao().delete(trash)
            }
            callback()
        }
    }

    fun signupUser(email:String, password:String, nickname: String, uri:String, callback: (Task<AuthResult>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val user = it.result.user?.let { userResult -> User(
                    userResult.uid,
                    userResult.email ?: "",
                    uri,
                    nickname
                    ) }
                if (user != null) {
                    user.lastUpdated = System.currentTimeMillis()
                    firebaseModel.addUser(user) {
                        callback(it)
                    }
                }
            }
    }

    fun updateUser(id:String, nickname: String, imageUri: String, callback: () -> Unit) {
        firebaseModel.updateUser(id, nickname, imageUri) {
            callback()
        }
    }

    fun getUserByEmail(email: String) {
        firebaseModel.getUserByEmail(email) {
            executor.execute {
                database.userDao().insert(it)
            }
            MyApplication.Globals.user = it
        }
    }

    fun getAuthorByEmail(email: String, callback: (User) -> Unit) {
        firebaseModel.getUserByEmail(email) {
            callback(it)
        }
    }

    fun getTrashById(id: String, callback: (Trash) -> Unit) {
        firebaseModel.getTrashById(id) {
            callback(it)
        }
    }
}