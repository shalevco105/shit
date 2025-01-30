package trashTalk.apps.trashTalk.modules

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import trashTalk.apps.trashTalk.models.Trash

class TrashViewModel:ViewModel() {
    var trashes: LiveData<MutableList<Trash>>? = null
    var myTrashes: LiveData<MutableList<Trash>>? = null
}