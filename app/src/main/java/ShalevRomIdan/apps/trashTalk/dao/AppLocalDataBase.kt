package trashTalk.apps.trashTalk.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import trashTalk.apps.trashTalk.base.MyApplication
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.models.User


@Database(entities = [Trash::class, User::class], version = 9)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun trashDao():TrashDao
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