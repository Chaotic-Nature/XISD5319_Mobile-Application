package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityLearnerProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LearnerProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLearnerProfileBinding
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learnerId = intent.extras?.getString("learner")
        val learnersRef = database.reference.child("learners")
        val questionAnswersRef = database.reference.child("screeningQuestions/$learnerId")
        var displayText = ""

        learnersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (pulledLearner in snapshot.children){
                    val learner : LearnerModel? = pulledLearner.getValue(LearnerModel::class.java)
                    if(learner != null && learner.id == learnerId){
                        binding.nameTv.text = learner.name + " " + learner.surname
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


        questionAnswersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (pulledQuestion in dataSnapshot.children) {
                    val q : ScreeningQuestionsModel? = pulledQuestion.getValue(ScreeningQuestionsModel::class.java)

                    if(q != null && learnerId != null){
                        displayText += "Q: ${q.question}\nA: ${q.answer}\n\n"
                        binding.addScreeningBtn.isEnabled = false
                    }
                    else{
                        displayText = "No screening record available."
                        binding.addScreeningBtn.isEnabled = true
                    }
                }

                binding.screeningResultsTv.text = displayText
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LearnerProfileActivity,
                    "Error displaying learner screening information.", Toast.LENGTH_LONG).show()
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