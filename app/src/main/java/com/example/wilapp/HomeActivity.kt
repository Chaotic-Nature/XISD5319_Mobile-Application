package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.findLearnerButton.setOnClickListener {
            val intent = Intent(this, FindLearnerActivity::class.java)
            startActivity(intent)
        }

        binding.addLearnerButton.setOnClickListener {
            val intent = Intent(this, AddLearnerActivity::class.java)
            startActivity(intent)
        }
        binding.logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish() // This will finish the current activity when the back button is pressed.
    }

}