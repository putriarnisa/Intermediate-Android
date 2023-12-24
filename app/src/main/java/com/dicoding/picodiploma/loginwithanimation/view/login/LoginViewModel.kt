package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {
    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to observe the login result
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Sealed class to represent different login results
    sealed class LoginResult {
        data class Success(val token: String) : LoginResult()
        object Error : LoginResult()
        object NetworkError : LoginResult()
    }

    // Function to initiate the login process
    fun login(email: String, password: String) {
        _isLoading.value = true
        apiService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    val token = responseBody?.loginResult?.token
                    if (token != null) {
                        userRepository.saveUser(UserModel(token, true))
                        _loginResult.value = LoginResult.Success(token)
                    } else {
                        _loginResult.value = LoginResult.Error
                    }
                } else {
                    _loginResult.value = LoginResult.Error
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _loginResult.value = LoginResult.NetworkError
            }
        })
    }
}