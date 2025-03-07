package com.iub.midterm_section2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.myapplication.midterm_section2.network.GitHubFile
import com.myapplication.midterm_section2.network.GitHubApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "PhotoRepository"

class PhotoRepository private constructor(
    context: Context?
) {
    private val coroutineScope: CoroutineScope = GlobalScope

    private val githubApi: GitHubApi
    private val token = "Bearer <Token>"
    private val owner = "B590-SUBAVENK"
    private val repo = "midtermsection2-part3-photostore"
    private val branch = "master"


    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubApi::class.java)
    }
    suspend fun saveImage(base64Image: String, filename: String) =
        withContext(Dispatchers.IO) {
            uploadImageToGitHub(base64Image, filename)
        }

    companion object {
        private var INSTANCE: PhotoRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PhotoRepository(context)
            }
        }

        fun get(): PhotoRepository {
            if (INSTANCE == null) {
                INSTANCE = PhotoRepository(context = null)
            }
            return INSTANCE!!
        }
    }


    private suspend fun uploadImageToGitHub(base64Image: String, filename: String) {
        val path = "$filename"
        val file = GitHubFile(
            message = "Add $filename",
            content = base64Image
        )

        try {
            val response = githubApi.uploadFile(token, owner, repo, path, file)
            if (response.isSuccessful) {
                Log.d(TAG, "File uploaded successfully: ${response.body()?.content?.path}")
            } else {
                Log.e(TAG, "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getImageUrl(fileName: String): String {
        return "https://api.github.com/repos/${owner}/${repo}/contents/${fileName}"
    }

    suspend fun fetchAndDecodeImage(apiUrl: String): Bitmap? {
        try {
            // 🔹 Extract the filename from the API URL
            val filename = apiUrl.substringAfterLast("/")

            // 🔹 Make API request to GitHub
            val response = githubApi.getFileContent(token, owner, repo, filename)

            if (response.isSuccessful) {
                val fileResponse = response.body()

                // 🔹 Ensure the response contains Base64-encoded data
                if (fileResponse?.encoding == "base64") {
                    val decodedBytes = Base64.decode(fileResponse.content, Base64.DEFAULT)
                    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } else {
                    Log.e("GitHub", "Unexpected response format. Encoding: ${fileResponse?.encoding}")
                }
            } else {
                Log.e("GitHub", "Error fetching image: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GitHub", "Exception: ${e.message}")
        }
        return null
    }
}