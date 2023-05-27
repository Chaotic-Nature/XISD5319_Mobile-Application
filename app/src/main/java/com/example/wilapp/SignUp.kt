package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import com.example.wilapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        val AccAlr = findViewById<Button>(R.id.button3)
        AccAlr.setOnClickListener {
            val Intent = Intent(this, Login::class.java)
            startActivity(Intent)

        }



        binding.button2.setOnClickListener{



            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()


            if ( email.isNotEmpty() && pass.isNotEmpty()) {


                firebaseAuth.createUserWithEmailAndPassword( email , pass) .addOnCompleteListener{
                    if(it.isSuccessful){

                        val intent = Intent(this , Login::class.java)
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