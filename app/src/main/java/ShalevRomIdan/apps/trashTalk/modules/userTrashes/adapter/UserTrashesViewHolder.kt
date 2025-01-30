package trashTalk.apps.trashTalk.modules.userTrashes.adapter

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.userTrashes.UserTrashesRecyclerViewActivity

class UserTrashesViewHolder(val itemView: View, val listener: UserTrashesRecyclerViewActivity.OnItemClickListener?, var trashes:List<Trash>?): RecyclerView.ViewHolder(itemView) {

    fun bind(trash: Trash?) {
        this.trash = trash
        nameTextView?.text = trash?.name

        Picasso.get().load(trash?.imageUrl)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .fit()
            .into(trashImage);
    }

    var nameTextView: TextView? = null
    var deleteTrashButton: ImageButton? = null
    var editTrashButton: ImageButton? = null
    var trashImage: ImageView? = null
    var trash: Trash? = null

    init {
        nameTextView = itemView.findViewById(R.id.lvUserTrashListName)
        editTrashButton = itemView.findViewById(R.id.editTrashButton)
        deleteTrashButton = itemView.findViewById(R.id.deleteTrashButton)
        trashImage = itemView.findViewById(R.id.imageViewUserTrash)

        editTrashButton?.setOnClickListener {
            //edit trash
            Log.i("TAG", "edited position: $adapterPosition")
            listener?.onTrashEdit(trash)
        }

        deleteTrashButton?.setOnClickListener {
            //deleteTrash
            Log.i("TAG", "deleted position: $adapterPosition")
            listener?.onTrashDelete(trash)
        }

        itemView.setOnClickListener {
            Log.i("TAG", "position: $adapterPosition")
            listener?.onTrashClick(trash)
        }
    }
}
