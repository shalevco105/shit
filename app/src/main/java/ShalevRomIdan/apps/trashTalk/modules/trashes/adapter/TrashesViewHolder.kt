package trashTalk.apps.trashTalk.modules.trashes.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.trashes.TrashesRecyclerViewActivity

class TrashesViewHolder(val itemView: View, val listener:TrashesRecyclerViewActivity.OnItemClickListener?, var trashes:List<Trash>?): RecyclerView.ViewHolder(itemView) {

    fun bind(trash: Trash?) {
        this.trash = trash
        nameTextView?.text = trash?.name

        Picasso.get().load(trash?.imageUrl)
            .centerCrop()
            .fit()
            .into(trashImage);
    }

    var nameTextView: TextView? = null
    var trashImage: ImageView? = null
    var trash: Trash? = null

    init {
        nameTextView = itemView.findViewById(R.id.lvTrashListName)
        trashImage = itemView.findViewById(R.id.imageView)

        itemView.setOnClickListener {
            Log.i("TAG", "position: $adapterPosition")
            listener?.onTrashClick(trash)
        }
    }
}
