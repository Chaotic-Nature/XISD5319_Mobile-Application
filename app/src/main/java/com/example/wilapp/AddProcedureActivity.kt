package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityAddProcedureBinding

class AddProcedureActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddProcedureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}