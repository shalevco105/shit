package trashTalk.apps.trashTalk.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import trashTalk.apps.trashTalk.models.Trash

@Dao
interface TrashDao {
    @Query("SELECT * FROM Trash")
    fun getAll() : LiveData<MutableList<Trash>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trash:Trash)

    @Delete
    fun delete(trash:Trash)

    @Query("SELECT * FROM Trash WHERE name =:name")
    fun getTrashByName(name:String): LiveData<Trash>

    @Query("SELECT * FROM Trash WHERE author =:email")
    fun getTrashesByUser(email:String): LiveData<MutableList<Trash>>
}