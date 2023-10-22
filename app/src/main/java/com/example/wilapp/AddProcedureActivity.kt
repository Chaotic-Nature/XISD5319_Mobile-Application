package com.example.wilapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityAddProcedureBinding
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class AddProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProcedureBinding
    private val database = Firebase.database
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val procedureReference = database.getReference("procedures")
        val learner = intent.extras?.getString("learner").toString()
        binding.dateDisplayTv.visibility = View.GONE

        binding.procedureCategorySpinner.adapter = populateSpinner()

        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.doctors,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.procedurePerformerSpinner.adapter = arrayAdapter

        binding.procedureDateBtn.setOnClickListener {
            showDatePicker()
        }


        binding.saveBtn.setOnClickListener {
            val category = binding.procedureCategorySpinner.selectedItem.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString().trim()
            val date = selectedDate
            val procedurePerformer = binding.procedurePerformerSpinner.selectedItem.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && procedurePerformer.isNotEmpty()) {
                val procedureId = procedureReference.child(learner)
                    .push()
                    .key
                if (procedureId != null) {
                    procedureReference.child(learner)
                        .child(procedureId)
                        .setValue(ProcedureModel(procedureId, category, description, procedurePerformer,date))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully added procedure",
                                Toast.LENGTH_LONG).show()
                            intent =Intent(this@AddProcedureActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learner)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show()
                        }
                }
            } else {
                Toast.makeText(
                    this,
                    "Ensure that all fields are not empty.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun populateSpinner(): ArrayAdapter<CharSequence> {
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.procedureCategories,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.GreenDatePickerDialogStyle, // Apply the custom style here
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
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
