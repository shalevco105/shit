package trashTalk.apps.trashTalk.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll() : LiveData<MutableList<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User WHERE email =:email")
    fun getUserByEmail(email:String): LiveData<User>
}