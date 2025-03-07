package com.myapplication.midterm_section2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.myapplication.midterm_section2.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // If user is already logged in, go to Posts Screen
        if (auth.currentUser != null) {
            goToPostsScreen()
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        return binding.root
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    goToPostsScreen()
                } else {
                    Log.e("LoginFragment", "signInWithEmail failed", task.exception)
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToPostsScreen() {
        findNavController().navigate(R.id.navigateToPosts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}