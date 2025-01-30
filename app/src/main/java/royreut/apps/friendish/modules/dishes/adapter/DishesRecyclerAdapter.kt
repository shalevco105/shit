package royreut.apps.friendish.modules.dishes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import royreut.apps.friendish.R
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.modules.dishes.DishesRecyclerViewActivity

class DishesRecyclerAdapter(var dishes:List<Dish>?): RecyclerView.Adapter<DishesViewHolder>() {

    var listener: DishesRecyclerViewActivity.OnItemClickListener? = null

    fun onDataUpdated(dishes: List<Dish>) {
        this.dishes = dishes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dish_layout_row, parent, false)
        return DishesViewHolder(itemView, listener, dishes)
    }

    override fun getItemCount(): Int = dishes?.size ?: 0

    override fun onBindViewHolder(holder: DishesViewHolder, position: Int) {
        val dish = dishes?.get(position)
        holder.bind(dish)
    }
}