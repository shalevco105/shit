package royreut.apps.friendish.modules.userDishes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import royreut.apps.friendish.R
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.modules.userDishes.UserDishesRecyclerViewActivity

class UserDishesRecyclerAdapter(var dishes:List<Dish>?): RecyclerView.Adapter<UserDishesViewHolder>() {

    var listener: UserDishesRecyclerViewActivity.OnItemClickListener? = null

    fun onDataUpdated(dishes: List<Dish>) {
        this.dishes = dishes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDishesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_dish_layout_row, parent, false)
        return UserDishesViewHolder(itemView, listener, dishes)
    }

    override fun getItemCount(): Int = dishes?.size ?: 0

    override fun onBindViewHolder(holder: UserDishesViewHolder, position: Int) {
        val dish = dishes?.get(position)
        holder.bind(dish)
    }
}