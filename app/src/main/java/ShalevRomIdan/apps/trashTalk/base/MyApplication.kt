package trashTalk.apps.trashTalk.base

import android.app.Application
import android.content.Context
import trashTalk.apps.trashTalk.models.User

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