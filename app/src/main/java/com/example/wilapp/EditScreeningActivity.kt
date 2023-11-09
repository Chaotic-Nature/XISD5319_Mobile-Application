package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wilapp.databinding.ActivityEditScreeningBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditScreeningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditScreeningBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var learner: String
    private val answers = mutableListOf<ScreeningQuestionsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        learner = intent.getStringExtra("learner") ?: ""
        database = FirebaseDatabase.getInstance()

        loadAnswersFromFirebase(database.reference.child("screeningQuestions").child(learner))

        binding.saveBtn.setOnClickListener {
            binding.editScrPb.visibility = View.VISIBLE
            binding.saveBtn.isEnabled = false

            saveEditedAnswers(database.reference.child("screeningQuestions").child(learner))
        }
        binding.cancelBtn.setOnClickListener {
            intent = Intent(this, LearnerProfileActivity::class.java)
            intent.putExtra("learner", learner)
            startActivity(intent)
            finish()
        }
    }

    private fun saveEditedAnswers(questionAnswersRef: DatabaseReference) {
        for (i in 0 until answers.size) {
            val switch = binding.editScreeningLayout.getChildAt(i * 2 + 1) as SwitchMaterial
            answers[i].answer = switch.isChecked
        }

        questionAnswersRef.setValue(answers)
            .addOnSuccessListener {
                binding.editScrPb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                Snackbar.make(
                    binding.root, "Answers saved successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
                intent = Intent(this, LearnerProfileActivity::class.java)
                intent.putExtra("learner", learner)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener { e ->
                binding.editScrPb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                Snackbar.make(
                    binding.root, "Failed to save answers: ${e.message}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }


    private fun loadAnswersFromFirebase(questionAnswersRef: DatabaseReference) {
        // Retrieve answers from Firebase for the specific learner
        questionAnswersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                answers.clear() // Clear existing answers

                for (snapshot in dataSnapshot.children) {
                    val answer = snapshot.getValue(ScreeningQuestionsModel::class.java)
                    if (answer != null) {
                        answers.add(answer)
                    }
                }

                // Populate the UI with answers
                populateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(
                    binding.root,
                    "Could not retrieve learners screening answers from the database",
                    Snackbar.LENGTH_SHORT
                ).show()
                Log.e("EDIT SCREENING", "Error: ${databaseError.message}")
            }
        })
    }

    private fun populateUI() {
        val editScreeningLayout = findViewById<LinearLayout>(R.id.editScreeningLayout)

        // Loop through answers and create UI elements for editing
        for (answer in answers) {
            val questionTextView = TextView(this)
            val switch = SwitchMaterial(this)

            questionTextView.text = answer.question
            questionTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
            switch.isChecked = answer.answer

            editScreeningLayout.addView(questionTextView)
            editScreeningLayout.addView(switch)
        }
    }
}
