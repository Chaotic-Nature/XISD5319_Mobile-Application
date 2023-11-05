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

        //Starts the sign up activity.
        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.forgotPasswordBtn.setOnClickListener {
            binding.emailTb.error = null

            val email = binding.emailTb.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailTb.error = "Email cannot be empty"
            }
            else{
                sendPasswordResetEmail(email)
            }

        }

        //Starts the sign in user method.
        binding.loginBtn.setOnClickListener {
            signInUser()
        }
    }
    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle password reset email sending failure
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInUser() {
        //The errors are set to null everytime the user clicks the sign in button.
        binding.emailTb.error = null
        binding.passwordTb.error = null

        val email = binding.emailTb.editText?.text.toString().trim()
        val password = binding.passwordTb.editText?.text.toString().trim()

        if (email.isEmpty()) {
            binding.emailTb.error = "Email cannot be empty"
        }
        else if (password.isEmpty()) {
            binding.passwordTb.error = "Password cannot be empty"
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
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
