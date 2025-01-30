package royreut.apps.friendish.modules.userDishes.adapter

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import royreut.apps.friendish.R
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.models.Model
import royreut.apps.friendish.modules.userDishes.UserDishesFragment
import royreut.apps.friendish.modules.userDishes.UserDishesFragmentDirections
import royreut.apps.friendish.modules.userDishes.UserDishesRecyclerViewActivity

class UserDishesViewHolder(val itemView: View, val listener: UserDishesRecyclerViewActivity.OnItemClickListener?, var dishes:List<Dish>?): RecyclerView.ViewHolder(itemView) {

    fun bind(dish: Dish?) {
        this.dish = dish
        nameTextView?.text = dish?.name

        Picasso.get().load(dish?.imageUrl)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .fit()
            .into(dishImage);
    }

    var nameTextView: TextView? = null
    var deleteDishButton: ImageButton? = null
    var editDishButton: ImageButton? = null
    var dishImage: ImageView? = null
    var dish: Dish? = null

    init {
        nameTextView = itemView.findViewById(R.id.lvUserDishListName)
        editDishButton = itemView.findViewById(R.id.editDishButton)
        deleteDishButton = itemView.findViewById(R.id.deleteDishButton)
        dishImage = itemView.findViewById(R.id.imageViewUserDish)

        editDishButton?.setOnClickListener {
            //edit dish
            Log.i("TAG", "edited position: $adapterPosition")
            listener?.onDishEdit(dish)
        }

        deleteDishButton?.setOnClickListener {
            //deleteDish
            Log.i("TAG", "deleted position: $adapterPosition")
            listener?.onDishDelete(dish)
        }

        itemView.setOnClickListener {
            Log.i("TAG", "position: $adapterPosition")
            listener?.onDishClick(dish)
        }
    }
}
