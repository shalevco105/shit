package trashTalk.apps.trashTalk.modules.userTrashes

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
import trashTalk.apps.trashTalk.base.MyApplication
import trashTalk.apps.trashTalk.databinding.FragmentUserTrashesBinding
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.models.Model
import trashTalk.apps.trashTalk.modules.TrashViewModel
import trashTalk.apps.trashTalk.modules.userTrashes.adapter.UserTrashesRecyclerAdapter

class UserTrashesFragment : Fragment() {

    var trashesRecyclerView : RecyclerView? = null
    var adapter: UserTrashesRecyclerAdapter? = null

    private var _binding:FragmentUserTrashesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:TrashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserTrashesBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[TrashViewModel::class.java]

        adapter = UserTrashesRecyclerAdapter(viewModel.myTrashes?.value)
        viewModel.myTrashes = Model.instance.getAllTrashesOfUser(MyApplication.Globals.user?.email ?: "")

        trashesRecyclerView = binding.rvUserTrashFragmentList
        trashesRecyclerView?.setHasFixedSize(true)

        // Set the layout manager
        trashesRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter?.listener = object : UserTrashesRecyclerViewActivity.OnItemClickListener {
            override fun onTrashClick(trash: Trash?) {
                Log.i("trash click", "trash: ${trash}")
                trash?.let {
                    val action = UserTrashesFragmentDirections.actionTrashesFragmentToShowcaseTrashFragment(it.name, it.imageUrl, it.author)
                    Navigation.findNavController(view).navigate(action)
                }
            }

            override fun onTrashDelete(trash: Trash?) {
                trash?.let {
                    Model.instance.deleteTrash(it) {
                        Log.i("delete trash", "trash: ${trash}")
                    }
                }
            }

            override fun onTrashEdit(trash: Trash?) {
                Log.i("edit trash", "trash: ${trash}")
                trash?.let {
                    val action =
                        UserTrashesFragmentDirections.actionUserTrashesFragmentToEditTrashFragment(it.id)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        trashesRecyclerView?.adapter = adapter

        viewModel.myTrashes?.observe(viewLifecycleOwner) {
            adapter?.onDataUpdated(it)
            adapter?.notifyDataSetChanged()
        }

        binding.userTrashesPullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.trashListLoadingState.observe(viewLifecycleOwner) {state ->
            binding.userTrashesPullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    fun reloadData() {
        Model.instance.refreshAllUserTrashes(MyApplication.Globals.user?.email ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}