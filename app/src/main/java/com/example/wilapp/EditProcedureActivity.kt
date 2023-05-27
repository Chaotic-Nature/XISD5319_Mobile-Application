package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityEditProcedureBinding

class EditProcedureActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProcedureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}