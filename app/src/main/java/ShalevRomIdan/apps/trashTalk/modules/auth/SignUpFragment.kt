package trashTalk.apps.trashTalk.modules.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.databinding.FragmentSignUpBinding
import trashTalk.apps.trashTalk.models.Model
import java.io.File
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.FileOutputStream

class SignUpFragment : Fragment() {
    private val placeholderImageSrc = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/681px-Placeholder_view_vector.svg.png"

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var emailTextView: TextView? = null
    private var passwordTextView: TextView? = null
    private var nicknameTextView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var profilePic: ImageView? = null
    private var profileImageUri: Uri? = null
    private var storageRef = Firebase.storage.reference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        emailTextView = binding.userNewEmail
        passwordTextView = binding.userNewPassword
        nicknameTextView = binding.nickname
        progressBar = binding.progressBarSignUp
        profilePic = binding.profilePic

        profilePic!!.setOnClickListener(::chooseProfilePicture)

        progressBar?.visibility = View.GONE

        val loginBtn = binding.signUpBtn
        loginBtn.setOnClickListener(::onSignUpWithFirebase)

        Picasso.get()
            .load(placeholderImageSrc)
            .into(profilePic);

        return view
    }

    fun onSignUpWithFirebase(view: View) {
        progressBar?.visibility = View.VISIBLE
        val email = emailTextView?.text.toString()
        val password = passwordTextView?.text.toString()
        val nickname = nicknameTextView?.text.toString()
        if(!(email.isNullOrBlank() ||
                    password.isNullOrBlank() ||
                    nickname.isNullOrBlank() ||
                    profileImageUri == null)) {
            uploadImageToCloudinary { uri ->
                Model.instance.signupUser(email, password, nickname, uri) { task ->
                    if (task.isSuccessful) {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_signUpFragment_to_loginFragment)
                    } else {
                        Toast.makeText(
                            view.context,
                            "Sign up failed. ${task.exception?.message}",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    progressBar?.visibility = View.GONE
                }
            }
            } else {
            Toast.makeText(
                view.context,
                "Please fill all fields",
                Toast.LENGTH_SHORT,
            ).show()

            progressBar?.visibility = View.GONE
        }
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
                    .into(profilePic);
            } else {
                Picasso.get()
                    .load(placeholderImageSrc)
                    .into(profilePic);
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

    private fun uploadImageToCloudinary(callback: (String) -> Unit) {
        val cloudinary = Cloudinary(ObjectUtils.asMap(
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
}