package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityFindLearnerBinding

class FindLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFindLearnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {
            TODO()
        }

        binding.learnerInformationCv.setOnClickListener{
            intent = Intent(this, LearnerProfileActivity::class.java)
            startActivity(intent)
        }

    }
}