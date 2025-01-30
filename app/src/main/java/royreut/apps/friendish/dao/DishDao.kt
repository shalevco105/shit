package royreut.apps.friendish.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import royreut.apps.friendish.models.Dish

@Dao
interface DishDao {
    @Query("SELECT * FROM Dish")
    fun getAll() : LiveData<MutableList<Dish>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg dish:Dish)

    @Delete
    fun delete(dish:Dish)

    @Query("SELECT * FROM Dish WHERE name =:name")
    fun getDishByName(name:String): LiveData<Dish>

    @Query("SELECT * FROM Dish WHERE author =:email")
    fun getDishesByUser(email:String): LiveData<MutableList<Dish>>
}