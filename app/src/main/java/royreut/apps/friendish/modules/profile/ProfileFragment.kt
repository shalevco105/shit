package royreut.apps.friendish.modules.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import royreut.apps.friendish.base.MyApplication
import royreut.apps.friendish.databinding.FragmentProfileBinding
import royreut.apps.friendish.models.Model

class ProfileFragment : Fragment() {
    private val placeholderImageSrc = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/681px-Placeholder_view_vector.svg.png"

    private var userEmailTextView: TextView? = null
    private var userProfileImageView: ImageView? = null
    private var nicknameEdit: TextView? = null
    private var progressBar: ProgressBar? = null
    private var profileImageUri: Uri? = null
    private var storageRef = Firebase.storage.reference;


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val user = MyApplication.Globals.user

        userEmailTextView = binding.showcaseUserEmail
        userProfileImageView = binding.showcaseUserProfileImage
        nicknameEdit = binding.nicknameEdit
        progressBar = binding.saveUserProgressBar

        progressBar?.visibility = View.GONE

        val loginBtn = binding.saveBtn
        loginBtn.setOnClickListener(::onSave)

        userProfileImageView!!.setOnClickListener(::chooseProfilePicture)

        nicknameEdit?.text = user?.nickname ?: ""
        userEmailTextView?.text = user?.email ?: "BOOP"
        Picasso.get().load(user?.imageUrl ?: placeholderImageSrc)
            .centerCrop()
            .fit()
            .into(userProfileImageView);

        return view
    }

    fun chooseProfilePicture(view: View) {
        // PICK INTENT picks item from data
        // and returned selected item
        val galleryIntent = Intent(Intent.ACTION_PICK)
        // here item is type of image
        galleryIntent.type = "image/*"
        // ActivityResultLauncher callback
        imagePickerActivityResult.launch(galleryIntent)
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.data?.data != null) {
                // getting URI of selected Image
                profileImageUri = result.data?.data

                Picasso.get()
                    .load(profileImageUri)
                    .into(userProfileImageView);
            } else {
                Picasso.get()
                    .load(MyApplication.Globals.user?.imageUrl ?: placeholderImageSrc)
                    .into(userProfileImageView);
            }
        }

    @SuppressLint("Range")
    private fun getFileName(context: Context?, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context?.contentResolver?.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun uploadImageToServer(callback: (String) -> Unit) {
        // extract the file name with extension
        val sd = getFileName(MyApplication.Globals.appContext, profileImageUri!!)

        // Upload Task with upload to directory 'file'
        // and name of the file remains same
        val uploadTask = storageRef.child("file/$sd").putFile(profileImageUri!!)

        // On success, download the file URL and display it
        uploadTask.addOnSuccessListener {
            // using glide library to display the image
            storageRef.child("file/$sd").downloadUrl.addOnSuccessListener {
                Log.e("Firebase", "download passed - ${it.path}")
                callback(it.toString())
            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading")
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail")
        }
    }

    fun onSave(view: View) {
        progressBar?.visibility = View.VISIBLE
        val id = MyApplication.Globals.user?.id
        val email = MyApplication.Globals.user?.email

        if(id != null
            && nicknameEdit?.text != null
            && email != null) {

            // check if image is changed
            if (profileImageUri != null) {
                uploadImageToServer { uri ->
                    Model.instance.updateUser(id, nicknameEdit?.text.toString(), uri) {
                        Model.instance.getUserByEmail(email)
                        progressBar?.visibility = View.GONE
                        Toast.makeText(
                            view.context,
                            "User update successfully!",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            } else {
                val prevImageUri = MyApplication.Globals.user?.imageUrl ?: ""
                Model.instance.updateUser(id, nicknameEdit?.text.toString(), prevImageUri) {
                    Model.instance.getUserByEmail(email)
                    progressBar?.visibility = View.GONE
                    Toast.makeText(
                        view.context,
                        "User update successfully!",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        } else {
            progressBar?.visibility = View.GONE
            Toast.makeText(
                view.context,
                "Error, try again later",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}