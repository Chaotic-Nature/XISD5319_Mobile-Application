package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.createAccBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            signInUser()
        }
    }

    private fun signInUser() {
        val email = binding.emailTb.editText?.text.toString().trim()
        val password = binding.passwordTb.editText?.text.toString().trim()

        binding.emailTb.error = null
        binding.passwordTb.error = null

        if (email.isEmpty()) {
            binding.emailTb.error = "Email cannot be empty"
        } else if (password.isEmpty()) {
            binding.passwordTb.error = "Password cannot be empty"
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        handleSignInError(task.exception)
                    }
                }
        }
    }

    private fun handleSignInError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Invalid user"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is FirebaseAuthRecentLoginRequiredException -> "Recent login required"
            else -> exception?.message
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
