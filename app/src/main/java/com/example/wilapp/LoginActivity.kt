package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Start the sign-up activity when the button is clicked.
        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Handle the "Forgot Password" button click.
        binding.forgotPasswordBtn.setOnClickListener {
            binding.emailTb.error = null

            val email = binding.emailTb.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailTb.error = "Email cannot be empty"
            } else {
                sendPasswordResetEmail(email)
            }
        }

        // Start the sign-in user method when the "Log In" button is clicked.
        binding.loginBtn.setOnClickListener {
            binding.emailTb.error = null
            binding.passwordTb.error = null

            val email = binding.emailTb.editText?.text.toString().trim()
            val password = binding.passwordTb.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailTb.error = "Email cannot be empty"
            } else if (password.isEmpty()) {
                binding.passwordTb.error = "Password cannot be empty"
            } else {
                signInUser(email, password)
            }
        }
    }

    // Send a password reset email for the specified email.
    private fun sendPasswordResetEmail(email: String) {
        binding.loginPb.visibility = View.VISIBLE
        binding.forgotPasswordBtn.isEnabled = false
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    binding.loginPb.visibility = View.GONE
                    binding.forgotPasswordBtn.isEnabled = true
                    Snackbar.make(binding.root,
                        "Password reset email sent",
                        Snackbar.LENGTH_SHORT).show()
                } else {
                    // Handle password reset email sending failure
                    binding.loginPb.visibility = View.GONE
                    binding.forgotPasswordBtn.isEnabled = true
                    Snackbar.make(binding.root,
                        "Failed to send password reset email",
                        Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    // Sign in the user with the specified email and password.
    private fun signInUser(email: String, password: String) {
        binding.loginPb.visibility = View.VISIBLE
        binding.loginBtn.isEnabled = false
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.loginPb.visibility = View.GONE
                    binding.loginBtn.isEnabled = true
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    binding.loginPb.visibility = View.GONE
                    binding.loginBtn.isEnabled = true
                    handleSignInError(task.exception)
                }
            }
    }

    // Handle and display errors during sign-in.
    private fun handleSignInError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Invalid user"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is FirebaseAuthRecentLoginRequiredException -> "Recent login required"
            else -> exception?.message
        }
        if (errorMessage != null) {
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        }
    }
}
