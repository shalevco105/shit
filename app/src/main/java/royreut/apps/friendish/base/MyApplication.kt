package royreut.apps.friendish.base

import android.app.Application
import android.content.Context
import royreut.apps.friendish.models.User

class MyApplication : Application() {

    object Globals {
        var appContext: Context? = null
        var user:User? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}