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
    }
}