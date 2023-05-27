package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityAddScreeningBinding

class AddScreeningActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddScreeningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}