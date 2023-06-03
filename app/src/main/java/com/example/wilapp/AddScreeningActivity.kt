package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            val list = populateList(learner)
            var successCount = 0
            val totalQuestions = list.size

            for (question in list) {
                wellnessMobileClinicRef.child(learner)
                    .push()
                    .setValue(question)
                    .addOnSuccessListener {
                        successCount++
                        if (successCount == totalQuestions) {
                            Toast.makeText(this, "Successfully added learner screening", Toast.LENGTH_LONG).show()
                            intent = Intent(this@AddScreeningActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learner)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show()
                    }
            }
        }

    }
    private fun populateList(learner: String): List<ScreeningQuestionsModel> {
        return mutableListOf(
            ScreeningQuestionsModel(learner, getString(R.string.q1_1), binding.qtn11S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q1_2), binding.qtn12S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q1_3), binding.qtn13S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q1_4), binding.qtn14S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q1_5), binding.qtn15S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q2_1), binding.qtn21S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q2_2), binding.qtn22S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q2_3), binding.qtn23S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q2_4), binding.qtn24S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q2_5), binding.qtn25S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q3_1), binding.qtn31S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q3_2), binding.qtn32S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q3_3), binding.qtn33S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q3_4), binding.qtn34S.isChecked),
            ScreeningQuestionsModel(learner, getString(R.string.q3_5), binding.qtn35S.isChecked),
        )
    }
}



