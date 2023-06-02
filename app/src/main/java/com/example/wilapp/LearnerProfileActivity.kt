package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityLearnerProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LearnerProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLearnerProfileBinding
    private val database = Firebase.database
    private val wellnessMobileClinicRef = database.getReference("learners")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learnerId = intent.extras?.getString("learner")

        wellnessMobileClinicRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (pulledLearner in snapshot.children){
                    val learner : LearnerModel? = pulledLearner.getValue(LearnerModel::class.java)
                    if(learner != null && learner.id == learnerId){
                        binding.nameTv.text = learner.name + learner.surname
                        binding.ageTv.text = learner.age.toString()
                        binding.sexTv.text = learner.sex
                        binding.idTv.text = learner.id
                        binding.schoolTv.text = learner.school
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LearnerProfileActivity,
                    "Failed to read value. ${error.toException()}", Toast.LENGTH_LONG).show()
            }
        })

        binding.editScreeningBtn.setOnClickListener {
            intent = Intent(this, EditScreeningActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
        }

        binding.addScreeningBtn.setOnClickListener {
            intent = Intent(this, AddScreeningActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
        }

        binding.editProcedureBtn.setOnClickListener {
            intent = Intent(this, EditProcedureActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
        }

        binding.addProcedureBtn.setOnClickListener {
            intent = Intent(this, AddProcedureActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
        }

        binding.returnBtn.setOnClickListener {
            finish()
        }
    }
}