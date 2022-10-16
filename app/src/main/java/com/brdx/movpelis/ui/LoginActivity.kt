package com.brdx.movpelis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.databinding.ActivityLoginBinding
import com.brdx.movpelis.domain.login.LoginRepoImpl
import com.brdx.movpelis.presentation.login.LoginViewModel
import com.brdx.movpelis.presentation.login.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val context by lazy { this@LoginActivity }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            LoginRepoImpl(
                RetrofitClient.webservice
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.btnActionLogin.setOnClickListener {
            val user = binding.etFieldUsername.editText?.text.toString()
            val password = binding.etFieldPassword.editText?.text.toString()

            if (user.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "You must fill in all fields", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            actionLogin(user, password)
        }
    }

    private fun actionLogin(user: String, password: String) {
        viewModel.signIn(user, password).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(binding.root, "Loading...", Snackbar.LENGTH_SHORT)
                        .show()
                }
                is Resource.Success -> {
                    val isSuccess = resource.data

                    if (isSuccess) {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                is Resource.Failure -> {
                    val exception = resource.exception
                    Snackbar.make(binding.root, (exception.message ?: "Error unexpected").toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}