package royreut.apps.friendish.modules

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import royreut.apps.friendish.models.Dish

class DishViewModel:ViewModel() {
    var dishes: LiveData<MutableList<Dish>>? = null
    var myDishes: LiveData<MutableList<Dish>>? = null
}