package trashTalk.apps.trashTalk.modules.profile

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
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import trashTalk.apps.trashTalk.base.MyApplication
import trashTalk.apps.trashTalk.databinding.FragmentProfileBinding
import trashTalk.apps.trashTalk.models.Model
import java.io.File
import java.io.FileOutputStream

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

    private fun uploadImageToCloudinary(callback: (String) -> Unit) {
        val cloudinary = Cloudinary(
            ObjectUtils.asMap(
            "cloud_name", "dy5xyzlhm",
            "api_key", "576721329452639",
            "api_secret", "xiPVujuY3tz3RxBqcZ8futbNVp8"
        ))

        val filePath = getFilePathFromUri(profileImageUri!!) // Convert URI to File path

        Thread {
            try {
                val result = cloudinary.uploader().upload(File(filePath), ObjectUtils.emptyMap())
                val imageUrl = result["secure_url"] as String
                Log.e("Cloudinary", "Upload success - $imageUrl")
                callback(imageUrl)
            } catch (e: Exception) {
                Log.e("Cloudinary", "Image Upload failed: ${e.message}")
            }
        }.start()
    }

    private fun getFilePathFromUri(uri: Uri): String {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_file")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
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
                uploadImageToCloudinary { uri ->
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