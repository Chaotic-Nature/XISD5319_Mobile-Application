package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityEditScreeningBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditScreeningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditScreeningBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var learner: String
    private val answers = mutableListOf<ScreeningQuestionsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        learner = intent.getStringExtra("learner") ?: ""

        val questionAnswersRef = database.reference.child("screeningQuestions").child(learner)

        binding.saveBtn.setOnClickListener {
            saveEditedAnswers(questionAnswersRef)
            intent = Intent(this, LearnerProfileActivity::class.java)
            intent.putExtra("learner", learner)
            startActivity(intent)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            intent = Intent(this, LearnerProfileActivity::class.java)
            intent.putExtra("learner", learner)
            startActivity(intent)
            finish()
        }

        loadAnswersFromFirebase(questionAnswersRef)
    }

    private fun saveEditedAnswers(questionAnswersRef: DatabaseReference) {
        for (i in 0 until answers.size) {
            val switch = binding.editScreeningLayout.getChildAt(i * 2 + 1) as Switch
            answers[i].answer = switch.isChecked
        }

        // Now, you can update the answers directly without relying on the 'id' property
        questionAnswersRef.setValue(answers)

        // Provide feedback to the user
        Toast.makeText(this, "Answers saved successfully", Toast.LENGTH_SHORT).show()
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
                // Handle error
            }
        })
    }

    private fun populateUI() {
        val editScreeningLayout = findViewById<LinearLayout>(R.id.editScreeningLayout)

        // Loop through answers and create UI elements for editing
        for (answer in answers) {
            val questionTextView = TextView(this)
            questionTextView.text = answer.question

            val switch = Switch(this)
            switch.isChecked = answer.answer

            editScreeningLayout.addView(questionTextView)
            editScreeningLayout.addView(switch)
        }
    }
}
