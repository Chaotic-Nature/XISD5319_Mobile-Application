package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityEditScreeningBinding

class EditScreeningActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditScreeningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}