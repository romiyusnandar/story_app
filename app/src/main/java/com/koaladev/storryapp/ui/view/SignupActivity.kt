package com.koaladev.storryapp.ui.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.koaladev.storryapp.databinding.ActivitySignupBinding
import com.koaladev.storryapp.ui.viewmodel.SignupViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.passwordEditText.setAsPassword()
        setupAction()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signup(name, email, password) { isSuccess, msg ->
                    binding.signupButton.startLoading()
                    if (isSuccess) {
                        binding.signupButton.doResult(true)
                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage("Akun untuk $email sudah jadi nih. Yuk, login sekarang.")
                            setPositiveButton("Lanjut") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        binding.signupButton.doResult(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Oops!")
                            setMessage(msg + ", coba gunakan akun lain " ?: "Terjadi kesalahan saat mendaftar")
                            setPositiveButton("Ok", null)
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }
}