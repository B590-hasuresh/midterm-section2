package com.myapplication.midterm_section2

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.myapplication.midterm_section2.databinding.FragmentCreateBinding
import com.myapplication.midterm_section2.model.Post
import com.myapplication.midterm_section2.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "CreateFragment"
class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private var photoUri : Uri? = null
    private var signedInUser: User? = null
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("CreateFragment", "Selected URI")
        // Inflate the layout for this fragment
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        firestoreDb = FirebaseFirestore.getInstance()
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("CreateFragment", "Selected URI: $uri")
                photoUri = uri
                binding.imageView.setImageURI(uri)
            } else {
                Log.d("CreateFragment", "No media selected")
            }
        }
        binding.btnPickImage.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSubmit.setOnClickListener{
            saveThePost()
        }

        getTheCurrentUser()
        return binding.root
    }
    private fun getTheCurrentUser() {
        Log.d("CreateFragment","into get functions")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "User is not logged in! Skipping Firestore query.")
            return
        }

        Log.d(TAG, "Fetching user from Firestore with UID: ${currentUser.uid}")

        firestoreDb.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.d(TAG, "Signed-in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failure fetching signed-in user", exception)
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun saveThePost() {

        val post = Post(
            binding.etDescription.text.toString(),
            imageUrl = "",
            System.currentTimeMillis(),
            signedInUser
        )

        firestoreDb.collection(  "posts").add(post).addOnCompleteListener {
            this.findNavController().navigate(R.id.navigate_to_postsFragment)
        }

    }
}