package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.wilapp.databinding.ActivityAddScreeningBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddScreeningActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddScreeningBinding
    private val database = Firebase.database
    private var wellnessMobileClinicRef = database.getReference("screeningQuestions")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learner = intent.extras?.getString("learner").toString()

        binding.saveBtn.setOnClickListener {

            //
            val q1 = Pair(getString(R.string.q1_1),binding.qtn11S.isChecked)
            val q2 = Pair(getString(R.string.q1_2),binding.qtn12S.isChecked)
            val q3 = Pair(getString(R.string.q1_3),binding.qtn13S.isChecked)
            val q4 = Pair(getString(R.string.q1_4),binding.qtn14S.isChecked)
            val q5 = Pair(getString(R.string.q1_5),binding.qtn15S.isChecked)

            //
            val q6 = Pair(getString(R.string.q2_1),binding.qtn21S.isChecked)
            val q7 = Pair(getString(R.string.q2_2),binding.qtn22S.isChecked)
            val q8 = Pair(getString(R.string.q2_3),binding.qtn23S.isChecked)
            val q9 = Pair(getString(R.string.q2_4),binding.qtn24S.isChecked)
            val q10 = Pair(getString(R.string.q2_5),binding.qtn25S.isChecked)

            //
            val q11 = Pair(getString(R.string.q3_1),binding.qtn31S.isChecked)
            val q12 = Pair(getString(R.string.q3_2),binding.qtn32S.isChecked)
            val q13 = Pair(getString(R.string.q3_3),binding.qtn13S.isChecked)
            val q14 = Pair(getString(R.string.q3_4),binding.qtn34S.isChecked)
            val q15 = Pair(getString(R.string.q3_5),binding.qtn35S.isChecked)

            saveAnswersToFirebase(learner, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15)
        }
    }

    private fun saveAnswersToFirebase(userId: String, vararg answers: Pair<String, Boolean>) {
        for ((index, pair) in answers.withIndex()) {
            val questionText = pair.first
            val questionId = "question${index + 1}"
            val answer = pair.second

            val questionRef = wellnessMobileClinicRef.child("screeningQuestions").child(userId).child(questionId)
            questionRef.child("question").setValue(questionText)
            questionRef.child("answer").setValue(answer)
        }
    }

}



