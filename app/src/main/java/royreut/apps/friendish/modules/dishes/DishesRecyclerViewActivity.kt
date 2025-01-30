package royreut.apps.friendish.modules.dishes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import royreut.apps.friendish.R
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.models.Model
import royreut.apps.friendish.modules.dishes.adapter.DishesRecyclerAdapter

class DishesRecyclerViewActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes_recycler_view)
    }

    interface OnItemClickListener {
        fun onDishClick(dish:Dish?)
    }

    override fun onResume() {
        super.onResume()
    }
}