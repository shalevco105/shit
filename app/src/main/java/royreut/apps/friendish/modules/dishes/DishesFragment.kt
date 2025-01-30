package royreut.apps.friendish.modules.dishes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import royreut.apps.friendish.databinding.FragmentDishesBinding
import royreut.apps.friendish.models.Dish
import royreut.apps.friendish.models.Model
import royreut.apps.friendish.modules.DishViewModel
import royreut.apps.friendish.modules.dishes.adapter.DishesRecyclerAdapter

class DishesFragment : Fragment() {

    var dishesRecyclerView : RecyclerView? = null
    var adapter:DishesRecyclerAdapter? = null

    private var _binding:FragmentDishesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:DishViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDishesBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[DishViewModel::class.java]

        adapter = DishesRecyclerAdapter(viewModel.dishes?.value)
        viewModel.dishes = Model.instance.getAllDishes()

        dishesRecyclerView = binding.rvDishFragmentList
        dishesRecyclerView?.setHasFixedSize(true)

        // Set the layout manager
        dishesRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter?.listener = object : DishesRecyclerViewActivity.OnItemClickListener {
            override fun onDishClick(dish: Dish?) {
                Log.i("TAG", "dish: ${dish}")
                dish?.let {
                    val action = DishesFragmentDirections.actionDishesFragmentToShowcaseDishFragment(it.name, it.recipe, it.imageUrl, it.author)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        dishesRecyclerView?.adapter = adapter

        viewModel.dishes?.observe(viewLifecycleOwner) {
            adapter?.onDataUpdated(it)
            adapter?.notifyDataSetChanged()
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.dishListLoadingState.observe(viewLifecycleOwner) {state ->
            binding.pullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    fun reloadData() {
        Model.instance.refreshAllDishes()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}