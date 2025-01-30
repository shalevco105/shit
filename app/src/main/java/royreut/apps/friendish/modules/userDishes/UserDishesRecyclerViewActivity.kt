package royreut.apps.friendish.modules.userDishes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import royreut.apps.friendish.R
import royreut.apps.friendish.models.Dish

class UserDishesRecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dishes_recycler_view)
    }

    interface OnItemClickListener {
        fun onDishClick(dish: Dish?)
        fun onDishDelete(dish: Dish?)
        fun onDishEdit(dish: Dish?)

    }

    override fun onResume() {
        super.onResume()
    }
}