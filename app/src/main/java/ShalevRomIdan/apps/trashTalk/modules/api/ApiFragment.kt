package trashTalk.apps.trashTalk.modules.api

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import trashTalk.apps.trashTalk.repository.CitiesRepository
import trashTalk.apps.trashTalk.databinding.FragmentApiBinding

class ApiFragment : Fragment() {
    private lateinit var citiesRepository: CitiesRepository
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
            citiesRepository = CitiesRepository()
            citiesRepository.getCities(qText.text.toString()) { cities ->
                if(cities?.get(0)?.name != null) {
                    result.text = cities.get(0).name;
                } else {
                    result.text = "No cities found"
                }

                progressBar?.visibility = View.GONE
            }
        }

        return view
    }
}