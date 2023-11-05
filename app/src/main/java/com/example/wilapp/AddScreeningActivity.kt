package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.TextView
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

        for (question in resources.getStringArray(R.array.eyeScreeningQuestions)) {
            val switch = SwitchMaterial(this)
            switch.text = "Question: $question"
            binding.eyeQuestionnaireLayout.addView(switch)
        }

        for (question in resources.getStringArray(R.array.earScreeningQuestions)) {

            val switch = SwitchMaterial(this)
            switch.text = "Question: $question"
            binding.earQuestionnaireLayout.addView(switch)
        }

        for (question in resources.getStringArray(R.array.throatScreeningQuestions)) {
            val switch = SwitchMaterial(this)
            switch.text = "Question: $question"

            binding.throatQuestionnaireLayout.addView(switch)
        }


        binding.saveBtn.setOnClickListener {
            val answers = mutableListOf<ScreeningQuestionsModel>()
            var successCount = 0

            for (i in 0 until binding.eyeQuestionnaireLayout.childCount) {
                val switch = binding.eyeQuestionnaireLayout.getChildAt(i) as SwitchMaterial
                answers.add(ScreeningQuestionsModel(learner, switch.text.toString() , switch.isChecked))
            }

            for (i in 0 until binding.earQuestionnaireLayout.childCount) {
                val switch = binding.earQuestionnaireLayout.getChildAt(i) as SwitchMaterial
                answers.add(ScreeningQuestionsModel(learner, switch.text.toString() , switch.isChecked))
            }

            for (i in 0 until binding.throatQuestionnaireLayout.childCount) {
                val switch = binding.throatQuestionnaireLayout.getChildAt(i) as SwitchMaterial
                answers.add(ScreeningQuestionsModel(learner, switch.text.toString() , switch.isChecked))
            }

            for (answer in answers) {
                wellnessMobileClinicRef.child(learner)
                    .push()
                    .setValue(answer)
                    .addOnSuccessListener {
                        successCount++
                        if (successCount == answers.size) {
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

        binding.cancelBtn.setOnClickListener {
            intent = Intent(this, LearnerProfileActivity::class.java)
            intent.putExtra("learner", learner)
            startActivity(intent)
            finish()
        }

    }
}



