package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.example.wilapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.button2.setOnClickListener{

            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()


            if ( email.isNotEmpty() && pass.isNotEmpty()) {


                firebaseAuth.signInWithEmailAndPassword( email , pass) .addOnCompleteListener{
                    if(it.isSuccessful){

                        val intent = Intent(this , HomePage::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            } else {

                Toast.makeText(this,"You have left fields empty", Toast.LENGTH_SHORT).show()
            }



        }


    }
}