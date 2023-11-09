package com.example.wilapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityAddProcedureBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProcedureBinding
    private lateinit var database : FirebaseDatabase
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learner = intent.extras?.getString("learner").toString()
        database = FirebaseDatabase.getInstance()

        binding.dateDisplayTv.visibility = View.GONE

        binding.procedureCategoryAcv.setAdapter(populateDropDown(R.array.procedureCategories))
        binding.procedurePerformerAcv.setAdapter(populateDropDown(R.array.doctors))

        binding.procedureDateBtn.setOnClickListener {
            showDatePicker()
        }


        binding.saveBtn.setOnClickListener {
            binding.addProcedurePb.visibility = View.VISIBLE
            binding.saveBtn.isEnabled = false
            val category = binding.procedureCategory.editText?.text.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString().trim()
            val date = selectedDate
            val procedurePerformer = binding.procedurePerformer.editText?.text.toString().trim()

            if (category.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && procedurePerformer.isNotEmpty()) {
                val procedureId = database.getReference("procedures").child(learner)
                    .push()
                    .key
                if (procedureId != null) {
                    database.getReference("procedures").child(learner)
                        .child(procedureId)
                        .setValue(ProcedureModel(procedureId, category, description, procedurePerformer,date))
                        .addOnSuccessListener {
                            binding.addProcedurePb.visibility = View.GONE
                            binding.saveBtn.isEnabled = true
                            Snackbar.make(binding.root, "Successfully added procedure",
                                Snackbar.LENGTH_LONG).show()
                            val intent =Intent(this@AddProcedureActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learner)
                            intent.putExtra("procedureId", procedureId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            binding.addProcedurePb.visibility = View.GONE
                            binding.saveBtn.isEnabled = true
                            Snackbar.make(binding.root, "Failed to add procedure",
                                Snackbar.LENGTH_LONG).show()
                        }
                }
            } else {
                binding.addProcedurePb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                Snackbar.make(binding.root,"Ensure that all fields are not empty.",
                    Snackbar.LENGTH_LONG).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun populateDropDown(stringArrayId: Int): ArrayAdapter<String> {
        return ArrayAdapter(
            this@AddProcedureActivity,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(stringArrayId)
        )
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                selectedDate = formattedDate
                binding.dateDisplayTv.text = selectedDate
                binding.dateDisplayTv.visibility = View.VISIBLE
            },
            year, month, day
        )
        datePickerDialog.show()
    }

}
