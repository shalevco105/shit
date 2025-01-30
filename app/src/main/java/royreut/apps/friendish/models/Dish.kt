package royreut.apps.friendish.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import royreut.apps.friendish.base.MyApplication

@Entity
data class Dish(
    @PrimaryKey val id:String,
    val name:String,
    val recipe:String,
    var isChecked:Boolean,
    var author:String,
    var imageUrl: String,
    var lastUpdated:Long? = null) {

    companion object {
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val RECIPE_KEY = "recipe"
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

        var userDishesLastUpdated:Long
            get() {
                return MyApplication
                    .Globals
                    .appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED_USER_DISHES,0) ?:0
            }
            set (value) {
                MyApplication
                    .Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putLong(GET_LAST_UPDATED_USER_DISHES, value)?.apply()
            }
        const val LAST_UPDATED:String = "lastUpdated"
        const val GET_LAST_UPDATED:String = "get_last_updated_dish"
        const val GET_LAST_UPDATED_USER_DISHES:String = "get_last_updated_user_dishes"

        fun fromJSON(json:Map<String, Any>):Dish {
            val id = json.get(ID_KEY) as? String ?: ""
            val name = json.get(NAME_KEY) as? String ?: ""
            val recipe = json.get(RECIPE_KEY) as? String?: ""
            val isChecked = json.get(IS_CHECKED_KEY) as? Boolean?: false
            val author = json.get(AUTHOR) as? String?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String?: "https://upload.wikimedia.org/wikipedia/commons/6/6e/Golde33443.jpg"
            val dish = Dish(id, name, recipe, isChecked, author, imageUrl)

            val timestamp:Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                dish.lastUpdated = it.seconds
            }

            return dish
        }
    }

    val json: Map<String, Any> get() {
        return hashMapOf(
            ID_KEY to id,
            NAME_KEY to name,
            RECIPE_KEY to recipe,
            IS_CHECKED_KEY to isChecked,
            IMAGE_URL_KEY to imageUrl,
            AUTHOR to author,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
    }
}