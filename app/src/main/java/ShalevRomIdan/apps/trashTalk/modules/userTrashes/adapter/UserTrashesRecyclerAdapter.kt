package trashTalk.apps.trashTalk.modules.userTrashes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.userTrashes.UserTrashesRecyclerViewActivity

class UserTrashesRecyclerAdapter(var trashes:List<Trash>?): RecyclerView.Adapter<UserTrashesViewHolder>() {

    var listener: UserTrashesRecyclerViewActivity.OnItemClickListener? = null

    fun onDataUpdated(trashes: List<Trash>) {
        this.trashes = trashes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTrashesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_trash_layout_row, parent, false)
        return UserTrashesViewHolder(itemView, listener, trashes)
    }

    override fun getItemCount(): Int = trashes?.size ?: 0

    override fun onBindViewHolder(holder: UserTrashesViewHolder, position: Int) {
        val trash = trashes?.get(position)
        holder.bind(trash)
    }
}