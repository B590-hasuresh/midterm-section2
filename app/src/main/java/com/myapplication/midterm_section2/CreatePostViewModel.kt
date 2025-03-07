package com.myapplication.midterm_section2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

class CreatePostViewModel : ViewModel() {
    private val photoRepository = PhotoRepository.get()

    suspend fun uploadImageToGitHub(base64Image: String, filename: String) {
        photoRepository.saveImage(base64Image, filename)
    }
}