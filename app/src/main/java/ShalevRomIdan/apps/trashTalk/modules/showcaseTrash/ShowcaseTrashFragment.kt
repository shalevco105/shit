package trashTalk.apps.trashTalk.modules.showcaseTrash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import trashTalk.apps.trashTalk.databinding.FragmentShowcaseTrashBinding
import trashTalk.apps.trashTalk.models.Model

class ShowcaseTrashFragment : Fragment() {
    private var trashNameTextView: TextView? = null
    private var trashAuthorTextView: TextView? = null
    private var trashImageView: ImageView? = null

    private var _binding: FragmentShowcaseTrashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShowcaseTrashBinding.inflate(inflater, container, false)
        val view = binding.root

        val trashName = arguments?.let {
            ShowcaseTrashFragmentArgs.fromBundle(it).TRASHNAME
        }

        val trashUrl = arguments?.let {
            ShowcaseTrashFragmentArgs.fromBundle(it).TRASHIMAGEURL
        }

        val author = arguments?.let {
            ShowcaseTrashFragmentArgs.fromBundle(it).TRASHAUTHOR
        }

        trashAuthorTextView = binding.authorNickName
        trashImageView = binding.showcaseTrashImage

        trashNameTextView?.text = trashName ?: "BOOP"
        Model.instance.getAuthorByEmail(author?:"") {
            trashAuthorTextView?.text = it.nickname
        }
        Picasso.get().load(trashUrl)
            .centerCrop()
            .fit()
            .into(trashImageView);

        return view
    }
}