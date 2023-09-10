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
            binding.emailTb.error = null
            binding.passwordTb.error = null
            binding.confirmPasswordTb.error = null

            val email = binding.emailTb.editText?.text.toString().trim()
            val password = binding.passwordTb.editText?.text.toString().trim()
            val confirmPassword = binding.confirmPasswordTb.editText?.text.toString().trim()


            if(email.isEmpty()){
                binding.emailTb.error = "Email cannot be empty"
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_LONG).show()
            }
            else if(password.isEmpty()){
                binding.passwordTb.error = "Password cannot be empty"
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show()
            }
            else if(confirmPassword.isEmpty()){
                binding.confirmPasswordTb.error = "Password cannot be empty"
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show()
            }
            else if(!passwordMatch(password, confirmPassword)){
                binding.passwordTb.error = "Passwords do not match"
                binding.confirmPasswordTb.error = "Passwords do not match"
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
            else{
                firebaseAuth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener{
                    if(it.isSuccessful){
                        intent = Intent(this , Login::class.java)
                        Toast.makeText(this, "Successfully created account", Toast.LENGTH_LONG).show()
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

    private fun passwordMatch(password : String, confirmPassword : String) : Boolean{
        return password == confirmPassword
    }
}