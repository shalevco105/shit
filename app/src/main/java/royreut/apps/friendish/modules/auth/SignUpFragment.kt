package royreut.apps.friendish.modules.auth

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
import royreut.apps.friendish.R
import royreut.apps.friendish.base.MyApplication
import royreut.apps.friendish.databinding.FragmentSignUpBinding
import royreut.apps.friendish.models.Model

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
            uploadImageToServer { uri ->
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

}