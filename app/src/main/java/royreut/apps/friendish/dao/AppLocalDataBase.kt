package royreut.apps.friendish.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import royreut.apps.friendish.base.MyApplication
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.models.User


@Database(entities = [Dish::class, User::class], version = 8)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun dishDao():DishDao
    abstract fun userDao():UserDao
}
object AppLocalDataBase {

    val db: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}