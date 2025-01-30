package royreut.apps.friendish.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import royreut.apps.friendish.base.MyApplication

@Entity
class User (
    val id:String,
    @PrimaryKey val email:String,
    var imageUrl: String,
    var nickname: String,
    var lastUpdated:Long? = null){

    companion object {
        const val ID_KEY = "id"
        const val EMAIL_KEY = "email"
        const val IMAGE_URL_KEY = "imageUrl"
        const val NICKNAME = "nickname"
        const val LAST_UPDATED:String = "lastUpdated"

        fun fromJSON(json:Map<String, Any>):User {
            val id = json.get(ID_KEY) as? String ?: ""
            val email = json.get(EMAIL_KEY) as? String ?: ""
            val nickname = json.get(NICKNAME) as? String ?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String?: "https://upload.wikimedia.org/wikipedia/commons/6/6e/Golde33443.jpg"
            val user = User(id,email, imageUrl, nickname)

            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                user.lastUpdated = it.seconds
            }

            return user
        }
    }

    val json: HashMap<String, Any?>
        get() {
        return hashMapOf(
            ID_KEY to id,
            EMAIL_KEY to email,
            IMAGE_URL_KEY to imageUrl,
            NICKNAME to nickname,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
    }
}