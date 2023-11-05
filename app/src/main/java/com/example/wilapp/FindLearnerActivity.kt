package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wilapp.databinding.ActivityFindLearnerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FindLearnerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindLearnerBinding
    private val database = Firebase.database
    private val learnerRef = database.getReference("learners")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val saIdRegex = """^\d{13}$""".toRegex()
        // Hiding the results label TextView.
        binding.resultsLabelTv.visibility = View.GONE

        binding.searchBtn.setOnClickListener {
            binding.idNumber.error = null
            val inputID = binding.idNumber.editText?.text.toString().trim()

            if (inputID.isEmpty()) {
                binding.idNumber.error = "ID number cannot be empty"
            } else if (!inputID.matches(saIdRegex)) {
                binding.idNumber.error = "Must be a valid ID number"
            } else {
                findLearner(inputID)
            }
        }
    }

    private fun findLearner(inputID: String) {
        learnerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val learnerList = mutableListOf<LearnerModel>()

                for (pulledLearner in snapshot.children) {
                    val learner: LearnerModel? = pulledLearner.getValue(LearnerModel::class.java)
                    if (learner != null && pulledLearner.key == inputID) {
                        learnerList.add(learner)
                    }
                }

                if (learnerList.isNotEmpty()) {
                    binding.resultsLabelTv.text = "Results"
                    binding.resultsLabelTv.visibility = View.VISIBLE
                    val adapter = LearnerModelAdapter(learnerList)
                    binding.learnerInfoRv.layoutManager = LinearLayoutManager(this@FindLearnerActivity)
                    binding.learnerInfoRv.adapter = adapter

                    adapter.setOnClickListener(object : LearnerModelAdapter.OnClickListener {
                        override fun onClick(position: Int, learner: LearnerModel) {
                            val intent = Intent(
                                this@FindLearnerActivity,
                                LearnerProfileActivity::class.java
                            )
                            intent.putExtra("learner", learner.id)
                            startActivity(intent)
                        }
                    })
                } else {
                    binding.resultsLabelTv.visibility = View.VISIBLE
                    binding.resultsLabelTv.text = "No results"
                    Toast.makeText(this@FindLearnerActivity,"Learner does not exist", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@FindLearnerActivity,
                    "Failed to read value. ${error.toException()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
