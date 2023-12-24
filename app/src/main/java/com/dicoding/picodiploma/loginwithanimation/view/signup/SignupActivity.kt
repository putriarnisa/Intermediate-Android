package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreferences
import com.dicoding.picodiploma.loginwithanimation.data.pref.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        val pref = UserPreferences.getInstance(dataStore)

        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiConfig.getApiService(), UserRepository(pref))
        )[RegisterViewModel::class.java]

        // Observe the registerResponse LiveData for changes
        registerViewModel.registerResponse.observe(this, Observer { response ->
            handleRegisterResponse(response)
        })
    }

    private fun handleRegisterResponse(response: FileUploadResponse) {
        if (!response.error) {
            // Registration successful, handle accordingly
            showSuccessDialog(response.message)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            // Registration failed, show an error message
            showErrorDialog(response.message)
        }
    }
    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage(message)
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

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Call the registerUser function in the ViewModel
            registerViewModel.registerUser(name, email, password)
        }
    }
}
