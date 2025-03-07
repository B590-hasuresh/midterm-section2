package com.myapplication.midterm_section2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.myapplication.midterm_section2.databinding.FragmentPostsBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val postViewModel : PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                postViewModel.posts.collect { posts ->
                    binding.rvPosts.adapter = PostHolder.PostsAdapter(posts)                }
            }
        }

        // Redirect to login if user is not authenticated
        if (auth.currentUser == null) {
            goToLoginScreen()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            goToLoginScreen()
        }

        binding.fabCreate.setOnClickListener {
            Log.d("PostsFragment", "FAB clicked - Navigating to CreateFragment")
            findNavController().navigate(R.id.navigate_to_createFragment)
        }
    }

    private fun goToLoginScreen() {
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }
}