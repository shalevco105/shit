package royreut.apps.friendish.modules.userDishes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import royreut.apps.friendish.base.MyApplication
import royreut.apps.friendish.databinding.FragmentUserDishesBinding
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.models.Model
import royreut.apps.friendish.modules.DishViewModel
import royreut.apps.friendish.modules.userDishes.adapter.UserDishesRecyclerAdapter

class UserDishesFragment : Fragment() {

    var dishesRecyclerView : RecyclerView? = null
    var adapter: UserDishesRecyclerAdapter? = null

    private var _binding:FragmentUserDishesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:DishViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserDishesBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[DishViewModel::class.java]

        adapter = UserDishesRecyclerAdapter(viewModel.myDishes?.value)
        viewModel.myDishes = Model.instance.getAllDishesOfUser(MyApplication.Globals.user?.email ?: "")

        dishesRecyclerView = binding.rvUserDishFragmentList
        dishesRecyclerView?.setHasFixedSize(true)

        // Set the layout manager
        dishesRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter?.listener = object : UserDishesRecyclerViewActivity.OnItemClickListener {
            override fun onDishClick(dish: Dish?) {
                Log.i("dish click", "dish: ${dish}")
                dish?.let {
                    val action = UserDishesFragmentDirections.actionDishesFragmentToShowcaseDishFragment(it.name, it.recipe, it.imageUrl, it.author)
                    Navigation.findNavController(view).navigate(action)
                }
            }

            override fun onDishDelete(dish: Dish?) {
                dish?.let {
                    Model.instance.deleteDish(it) {
                        Log.i("delete dish", "dish: ${dish}")
                    }
                }
            }

            override fun onDishEdit(dish: Dish?) {
                Log.i("edit dish", "dish: ${dish}")
                dish?.let {
                    val action =
                        UserDishesFragmentDirections.actionUserDishesFragmentToEditDishFragment(it.id)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        dishesRecyclerView?.adapter = adapter

        viewModel.myDishes?.observe(viewLifecycleOwner) {
            adapter?.onDataUpdated(it)
            adapter?.notifyDataSetChanged()
        }

        binding.userDishesPullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.dishListLoadingState.observe(viewLifecycleOwner) {state ->
            binding.userDishesPullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    fun reloadData() {
        Model.instance.refreshAllUserDishes(MyApplication.Globals.user?.email ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}