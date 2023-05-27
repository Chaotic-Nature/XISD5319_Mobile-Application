package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityAddLearnerBinding

class AddLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddLearnerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveBtn.setOnClickListener {
            TODO()
        }
    }
}