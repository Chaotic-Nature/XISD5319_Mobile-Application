package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*Getting the database instance associated with this app.*/
        firebaseAuth = FirebaseAuth.getInstance()

        //Takes user to the login page.
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener {
            signUpUser()
        }
    }

    /*Validates user sign up credentials and creates an account for them if their information
        is correct*/
    private fun signUpUser() {
        /*The signUpPb is a progress bar that acts as a "loading" icon that's being made visible.*/
        binding.signUpPb.visibility = View.VISIBLE
        binding.signUpBtn.isEnabled = false
        //Setting all textBox errors to null everytime the button is clicked.
        binding.emailTb.error = null
        binding.passwordTb.error = null
        binding.confirmPasswordTb.error = null

        //Getting user input.
        val email = binding.emailTb.editText?.text.toString().trim()
        val password = binding.passwordTb.editText?.text.toString().trim()
        val confirmPassword = binding.confirmPasswordTb.editText?.text.toString().trim()
        //Validating email and password.
        if (validateEmail(email) && validatePassword(password, confirmPassword)) {

            //Firebase built in async function that creates a user.
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    //Hiding the progress bar and going to the sign up activity.
                    binding.signUpPb.visibility = View.GONE
                    binding.signUpBtn.isEnabled = true
                    val intent = Intent(this, LoginActivity::class.java)
                    showMessage("Successfully created account")
                    startActivity(intent)
                    finish()
                } else {
                    //Hiding the progress bar and handling an exception.
                    binding.signUpPb.visibility = View.GONE
                    binding.signUpBtn.isEnabled = true
                    handleSignUpError(it.exception)
                }
            }
        }
    }
    //Displays an error if the email is empty.
    private fun validateEmail(email: String): Boolean {
        val emailPattern = ("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").toRegex()
        val flag: Boolean
        if (email.isEmpty()) {
            binding.emailTb.error = "Email cannot be empty"
            flag = false
        } else if(!email.matches(emailPattern)){
            binding.emailTb.error = "Invalid Email"
            flag = false
        } else{
            flag = true
        }
        return flag
    }
    //Displays an error if the password and confirm password fields are empty or don't match.
    private fun validatePassword(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty()) {
            binding.passwordTb.error = "Password cannot be empty"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordTb.error = "Password cannot be empty"
            return false
        }
        if (password != confirmPassword) {
            binding.passwordTb.error = "Passwords do not match"
            binding.confirmPasswordTb.error = "Passwords do not match"
            return false
        }
        return true
    }

    //Displays error messages for Firebase-specific exceptions.
    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password must be 6 or more characters"
            is FirebaseAuthUserCollisionException -> "Email is already in use"
            is FirebaseAuthEmailException -> "Invalid email address"
            else -> exception?.message
        }
        showMessage(errorMessage.toString())
    }
    //A helper function that makes showing SnackBar messages easier.
    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}