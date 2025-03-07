package com.myapplication.midterm_section2


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.myapplication.midterm_section2.databinding.FragmentPostsBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(requireContext())

        // Logout button click listener
        binding.btnLogout.setOnClickListener {
            auth.signOut()  // Logout user
            findNavController().navigate(R.id.loginFragment)  // Navigate to Login Screen
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}