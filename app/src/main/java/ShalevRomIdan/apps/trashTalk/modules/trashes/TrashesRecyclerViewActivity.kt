package trashTalk.apps.trashTalk.modules.trashes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash

class TrashesRecyclerViewActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trashes_recycler_view)
    }

    interface OnItemClickListener {
        fun onTrashClick(trash:Trash?)
    }

    override fun onResume() {
        super.onResume()
    }
}