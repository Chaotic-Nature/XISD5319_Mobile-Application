package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityLearnerProfileBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LearnerProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLearnerProfileBinding
    private val database = Firebase.database
    private var wellnessMobileClinicRef = database.getReference("learners")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editScreeningBtn.setOnClickListener {
            intent = Intent(this, EditScreeningActivity::class.java)
            startActivity(intent)
        }

        binding.addScreeningBtn.setOnClickListener {
            intent = Intent(this, AddScreeningActivity::class.java)
            startActivity(intent)
        }

        binding.editProcedureBtn.setOnClickListener {
            intent = Intent(this, EditProcedureActivity::class.java)
            startActivity(intent)
        }

        binding.addProcedureBtn.setOnClickListener {
            intent = Intent(this, AddProcedureActivity::class.java)
            startActivity(intent)
        }

        binding.returnBtn.setOnClickListener {
            finish()
        }
    }
}