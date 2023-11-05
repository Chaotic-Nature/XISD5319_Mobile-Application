package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Takes user to the login page
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener{
            signUpUser()
        }
    }

    /*Validates user sign up credentials and creates an account for them if their information
        is correct*/
    private fun signUpUser(){
        binding.emailTb.error = null
        binding.passwordTb.error = null
        binding.confirmPasswordTb.error = null

        val email = binding.emailTb.editText?.text.toString().trim()
        val password = binding.passwordTb.editText?.text.toString().trim()
        val confirmPassword = binding.confirmPasswordTb.editText?.text.toString().trim()

        if(email.isEmpty()){
            binding.emailTb.error = "Email cannot be empty"
        }
        else if(password.isEmpty()){
            binding.passwordTb.error = "Password cannot be empty"
        }
        else if(confirmPassword.isEmpty()){
            binding.confirmPasswordTb.error = "Password cannot be empty"
        }
        else if(!passwordMatch(password, confirmPassword)){
            binding.passwordTb.error = "Passwords do not match"
            binding.confirmPasswordTb.error = "Passwords do not match"
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener{
                if(it.isSuccessful){
                    intent = Intent(this , Login::class.java)
                    showMessage("Successfully created account")
                    startActivity(intent)
                    finish()

                } else {
                    handleSignUpError(it.exception)
                }
            }
        }
    }
    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password must be 6 or more characters"
            is FirebaseAuthUserCollisionException -> "Email already exists"
            else -> exception?.message
        }
        showMessage(errorMessage.toString())
    }

    private fun passwordMatch(password : String, confirmPassword : String) : Boolean{
        return password == confirmPassword
    }

    private fun showMessage(message : String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}