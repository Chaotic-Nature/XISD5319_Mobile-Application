package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }
        binding.signUpBtn.setOnClickListener{
            val email = binding.emailTb.editText?.text.toString().trim()
            val password = binding.passwordTb.editText?.text.toString().trim()

            binding.emailTb.error = null
            binding.passwordTb.error = null

            if(email.isEmpty()){
                binding.emailTb.error = "Email cannot be empty"
            }
            else if(password.isEmpty()){
                binding.passwordTb.error = "Password cannot be empty"
            }
            else{
                firebaseAuth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener{
                    if(it.isSuccessful){
                        intent = Intent(this , Login::class.java)
                        startActivity(intent)

                    } else {
                        handleSignUpError(it.exception)
                    }
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}