package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityEditScreeningBinding
import com.google.firebase.database.FirebaseDatabase

class EditScreeningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditScreeningBinding
    private val database = FirebaseDatabase.getInstance()
    private var wellnessMobileClinicRef = database.getReference("screeningQuestions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learnerId = intent.getStringExtra("learner")
        val questionAnswersRef = database.reference.child("screeningQuestions").child(learnerId!!)

        binding.saveBtn.setOnClickListener {
            questionAnswersRef.setValue(null)
                .addOnSuccessListener {
                    saveDataToFirebase(learnerId)
                }
                .addOnFailureListener {
                    Toast.makeText(this@EditScreeningActivity, "Error deleting existing data.", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveDataToFirebase(learnerId: String) {
        val list = populateList(learnerId)
        val totalQuestions = list.size
        var successCount = 0

        for (question in list) {
            wellnessMobileClinicRef.child(learnerId)
                .push()
                .setValue(question)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        successCount++
                        if (successCount == totalQuestions) {
                            Toast.makeText(
                                this@EditScreeningActivity,
                                "Successfully edited learner screening",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@EditScreeningActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learnerId)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@EditScreeningActivity, "Something went wrong.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun populateList(learner: String): List<ScreeningQuestionsModel> {
        return listOf(
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
            ScreeningQuestionsModel(learner, getString(R.string.q3_5), binding.qtn35S.isChecked)
        )
    }
}
