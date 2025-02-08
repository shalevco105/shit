package trashTalk.apps.trashTalk.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import trashTalk.apps.trashTalk.base.MyApplication

@Entity
data class Trash(
    @PrimaryKey val id:String,
    val name:String,
    val address: String = "",
    var isChecked:Boolean,
    var author:String,
    var imageUrl: String,
    var lastUpdated:Long? = null) {

    companion object {
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val ADDRESS_KEY = "address"
        const val IS_CHECKED_KEY = "isChecked"
        const val AUTHOR = "author"
        const val IMAGE_URL_KEY = "imageUrl"

        var lastUpdated:Long
            get() {
                return MyApplication
                    .Globals
                    .appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED,0) ?:0
            }
            set (value) {
                MyApplication
                    .Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putLong(GET_LAST_UPDATED, value)?.apply()
            }

        var userTrashesLastUpdated:Long
            get() {
                return MyApplication
                    .Globals
                    .appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED_USER_TRASHES,0) ?:0
            }
            set (value) {
                MyApplication
                    .Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putLong(GET_LAST_UPDATED_USER_TRASHES, value)?.apply()
            }
        const val LAST_UPDATED:String = "lastUpdated"
        const val GET_LAST_UPDATED:String = "get_last_updated_trash"
        const val GET_LAST_UPDATED_USER_TRASHES:String = "get_last_updated_user_trashes"

        fun fromJSON(json:Map<String, Any>):Trash {
            val id = json.get(ID_KEY) as? String ?: ""
            val name = json.get(NAME_KEY) as? String ?: ""
            val address = json.get(ADDRESS_KEY) as? String ?: ""
            val isChecked = json.get(IS_CHECKED_KEY) as? Boolean?: false
            val author = json.get(AUTHOR) as? String?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String?: "https://upload.wikimedia.org/wikipedia/commons/6/6e/Golde33443.jpg"
            val trash = Trash(id, name, address, isChecked, author, imageUrl)

            val timestamp:Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                trash.lastUpdated = it.seconds
            }

            return trash
        }
    }

    val json: Map<String, Any> get() {
        return hashMapOf(
            ID_KEY to id,
            NAME_KEY to name,
            ADDRESS_KEY to address,
            IS_CHECKED_KEY to isChecked,
            IMAGE_URL_KEY to imageUrl,
            AUTHOR to author,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
    }
}