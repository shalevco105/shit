package trashTalk.apps.trashTalk.modules.trashes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.trashes.TrashesRecyclerViewActivity

class TrashesRecyclerAdapter(var trashes:List<Trash>?): RecyclerView.Adapter<TrashesViewHolder>() {

    var listener: TrashesRecyclerViewActivity.OnItemClickListener? = null

    fun onDataUpdated(trashes: List<Trash>) {
        this.trashes = trashes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.trash_layout_row, parent, false)
        return TrashesViewHolder(itemView, listener, trashes)
    }

    override fun getItemCount(): Int = trashes?.size ?: 0

    override fun onBindViewHolder(holder: TrashesViewHolder, position: Int) {
        val trash = trashes?.get(position)
        holder.bind(trash)
    }
}