package com.koaladev.storryapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.koaladev.storryapp.R
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.pref.UserPreference
import com.koaladev.storryapp.data.pref.dataStore
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.retrofit.ApiConfig
import com.koaladev.storryapp.databinding.ActivityLoginBinding
import com.koaladev.storryapp.ui.viewmodel.LoginViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var repository: UserRepository
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userPreference = UserPreference.getInstance(dataStore)
        repository = UserRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfig.getApiService()
        )
        binding.passwordEditText.setAsPassword()

        setupAction()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password) { isSuccess, userId, name, email, token ->
                if (isSuccess) {
                    viewModel.saveSession(UserModel(userId, name, email, token, true))
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}