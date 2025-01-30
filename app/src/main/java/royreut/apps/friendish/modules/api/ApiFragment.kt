package royreut.apps.friendish.modules.api

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import royreut.apps.friendish.repository.RecipeIdeaRepository
import royreut.apps.friendish.databinding.FragmentApiBinding

class ApiFragment : Fragment() {
    private lateinit var recipeIdeaRepository: RecipeIdeaRepository
    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!

    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        val view = binding.root

        val result = binding.result
        val qText = binding.qText
        val btnSearch = binding.btnSearch
        progressBar = binding.progressBarApi

        progressBar?.visibility = View.GONE

        btnSearch.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            recipeIdeaRepository = RecipeIdeaRepository()
            recipeIdeaRepository.getIdeas(qText.text.toString()) { recipeIdeas ->
                if(recipeIdeas?.get(0)?.title != null) {
                    result.text = recipeIdeas.get(0).title.toString() ?: "no recipe found"
                } else {
                    result.text = "No recipe found"
                }

                progressBar?.visibility = View.GONE
            }
        }

        return view
    }
}