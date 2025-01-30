package trashTalk.apps.trashTalk.modules.userTrashes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash

class UserTrashesRecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_trashes_recycler_view)
    }

    interface OnItemClickListener {
        fun onTrashClick(trash: Trash?)
        fun onTrashDelete(trash: Trash?)
        fun onTrashEdit(trash: Trash?)

    }

    override fun onResume() {
        super.onResume()
    }
}