package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.FileUploadResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val apiService: ApiService) : ViewModel() {

    val registerResponse: MutableLiveData<FileUploadResponse> = MutableLiveData()

    fun registerUser(name: String, email: String, password: String) {
        apiService.register(name, email, password).enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    registerResponse.value = response.body()
                } else {
                    // Handle unsuccessful response
                    registerResponse.value = FileUploadResponse(error = true, message = "Registration failed")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                // Handle failure
                registerResponse.value = FileUploadResponse(error = true, message = "Registration failed")
            }
        })
    }
}

