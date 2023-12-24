package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreferences
import com.dicoding.picodiploma.loginwithanimation.data.pref.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val pref = UserPreferences.getInstance(dataStore)

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), UserRepository(pref))
        )[LoginViewModel::class.java]

        loginViewModel.loginResult.observe(this) { response ->
            handleLoginResponse(response)
        }

        // Example usage of the login function
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginViewModel.login(email, password)
        }

        userRepository = UserRepository(pref)
        userRepository.getUser().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun handleLoginResponse(response: LoginViewModel.LoginResult) {
        when (response) {
            is LoginViewModel.LoginResult.Success -> {
                // Login successful, handle accordingly
                "Login successful".showSuccessDialog()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            LoginViewModel.LoginResult.Error -> {
                // Login failed, show an error message
                showErrorDialog("Login failed")
            }
            LoginViewModel.LoginResult.NetworkError -> {
                // Network error, show an error message
                showErrorDialog("Network error")
            }
        }
    }

    private fun String.showSuccessDialog() {
        AlertDialog.Builder(this@LoginActivity)
            .setTitle("Success")
            .setMessage(this)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}