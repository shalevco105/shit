package trashTalk.apps.trashTalk.modules.trashes

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
import trashTalk.apps.trashTalk.databinding.FragmentTrashesBinding
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.models.Model
import trashTalk.apps.trashTalk.modules.TrashViewModel
import trashTalk.apps.trashTalk.modules.trashes.adapter.TrashesRecyclerAdapter

class TrashesFragment : Fragment() {

    var trashesRecyclerView : RecyclerView? = null
    var adapter:TrashesRecyclerAdapter? = null

    private var _binding:FragmentTrashesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:TrashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTrashesBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[TrashViewModel::class.java]

        adapter = TrashesRecyclerAdapter(viewModel.trashes?.value)
        viewModel.trashes = Model.instance.getAllTrashes()

        trashesRecyclerView = binding.rvTrashFragmentList
        trashesRecyclerView?.setHasFixedSize(true)

        // Set the layout manager
        trashesRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter?.listener = object : TrashesRecyclerViewActivity.OnItemClickListener {
            override fun onTrashClick(trash: Trash?) {
                Log.i("TAG", "trash: ${trash}")
                trash?.let {
                    val action = TrashesFragmentDirections.actionTrashesFragmentToShowcaseTrashFragment(it.name, it.recipe, it.imageUrl, it.author)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        trashesRecyclerView?.adapter = adapter

        viewModel.trashes?.observe(viewLifecycleOwner) {
            adapter?.onDataUpdated(it)
            adapter?.notifyDataSetChanged()
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.trashListLoadingState.observe(viewLifecycleOwner) {state ->
            binding.pullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    fun reloadData() {
        Model.instance.refreshAllTrashes()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}