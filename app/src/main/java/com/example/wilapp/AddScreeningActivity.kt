package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.example.wilapp.databinding.ActivityAddScreeningBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.FirebaseDatabase

class AddScreeningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddScreeningBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learner = intent.extras?.getString("learner").toString()
        database = FirebaseDatabase.getInstance()

        displayQuestions()
        setupSaveButton(learner)
        setupCancelButton(learner)
    }

    private fun displayQuestions() {
        addQuestionsToLayout(R.array.eyeScreeningQuestions, binding.eyeQuestionnaireLayout)
        addQuestionsToLayout(R.array.earScreeningQuestions, binding.earQuestionnaireLayout)
        addQuestionsToLayout(R.array.throatScreeningQuestions, binding.throatQuestionnaireLayout)
    }

    private fun addQuestionsToLayout(questionArrayId: Int, layout: LinearLayout) {
        resources.getStringArray(questionArrayId).forEach { questionText ->
            val switch = SwitchMaterial(this)
            val question = "Question: $questionText"
            switch.text = question
            layout.addView(switch)
        }
    }

    private fun setupSaveButton(learner: String) {
        binding.screeningPb.visibility = View.VISIBLE
        binding.saveBtn.isEnabled = false
        binding.saveBtn.setOnClickListener {
            val answers = mutableListOf<ScreeningQuestionsModel>()
            val successCount = answers.size

            val questionLayouts = listOf(
                binding.eyeQuestionnaireLayout,
                binding.earQuestionnaireLayout,
                binding.throatQuestionnaireLayout
            )

            questionLayouts.forEach { layout ->
                layout.forEach { childView ->
                    if (childView is SwitchMaterial) {
                        answers.add(ScreeningQuestionsModel(learner, childView.text.toString(), childView.isChecked))
                    }
                }
            }

            val databaseReference = database.getReference("screeningQuestions").child(learner)
            answers.forEach { answer ->
                databaseReference.push().setValue(answer)
                    .addOnSuccessListener {
                        if (successCount == answers.size - 1) {
                            binding.screeningPb.visibility = View.GONE
                            binding.saveBtn.isEnabled = true
                            showSuccessMessageAndNavigate(learner)
                        }
                    }
                    .addOnFailureListener {
                        binding.screeningPb.visibility = View.GONE
                        binding.saveBtn.isEnabled = true
                        showMessage("Something went wrong.")
                        Log.e("Screening Questions Save", "${it.message}")
                    }
            }
        }
    }

    private fun setupCancelButton(learner: String) {
        binding.cancelBtn.setOnClickListener {
            val intent = Intent(this, LearnerProfileActivity::class.java)
            intent.putExtra("learner", learner)
            startActivity(intent)
            finish()
        }
    }

    private fun showSuccessMessageAndNavigate(learner: String) {
        showMessage("Successfully added learner screening")
        val intent = Intent(this, LearnerProfileActivity::class.java)
        intent.putExtra("learner", learner)
        startActivity(intent)
        finish()
    }

    private fun showMessage(message : String) {
        Snackbar.make(binding.root,
            message,
            Snackbar.LENGTH_LONG).show()
    }
}



