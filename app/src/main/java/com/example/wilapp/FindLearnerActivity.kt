package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wilapp.databinding.ActivityFindLearnerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FindLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFindLearnerBinding
    private val database = Firebase.database
    private val wellnessMobileClinicRef = database.getReference("learners")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {

            val inputID = binding.idNumber.editText?.text.toString().trim()

            wellnessMobileClinicRef.addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val learnerList = mutableListOf<LearnerModel>()

                    for (pulledLearner in snapshot.children){
                        val learner : LearnerModel? = pulledLearner.getValue(LearnerModel::class.java)
                        if(learner != null && learner.id == inputID){
                            learnerList.add(learner)
                        }
                    }

                    val adapter = LearnerModelAdapter(learnerList)
                    binding.learnerInfoRv.layoutManager = LinearLayoutManager(this@FindLearnerActivity)

                    binding.learnerInfoRv.adapter = adapter

                    adapter.setOnClickListener(object :
                        LearnerModelAdapter.OnClickListener {
                        override fun onClick(position: Int, learner: LearnerModel) {
                            intent = Intent(this@FindLearnerActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learner.id)
                            startActivity(intent)
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(this@FindLearnerActivity,
                        "Failed to read value. ${error.toException()}", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}