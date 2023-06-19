package com.example.wilapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityEditProcedureBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class EditProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProcedureBinding
    private val database = Firebase.database
    private var selectedDate = ""
    private var learnerId = ""
    private var  procedureId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        learnerId = intent.getStringExtra("learnerId") ?: ""
        procedureId = intent.getStringExtra("procedureId") ?: ""
        var dataRef = database.getReference("procedures/$learnerId")

        binding.procedureCategorySpinner.adapter = populateSpinner()
        retrieveData()

        binding.saveBtn.setOnClickListener {
            val procedure = binding.procedureCategorySpinner.selectedItem.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString()
            val performer = binding.procedurePerformerTb.editText?.text.toString()
            val date = selectedDate

            if (procedure.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && performer.isNotEmpty()) {
                deleteData(procedureId)

                dataRef
                    .push()
                    .setValue(ProcedureModel("", procedure, description, performer,date))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully edited procedure",
                            Toast.LENGTH_LONG).show()
                        intent =Intent(this@EditProcedureActivity, LearnerProfileActivity::class.java)
                        intent.putExtra("learner", learnerId)
                        //startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "Ensure that all fields are not empty.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.procedureDateBtn.setOnClickListener {
            showDatePicker()
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun retrieveData() {
        val dataRef = database.getReference("procedures/$learnerId")

        dataRef.orderByChild(procedureId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (procedureSnapshot in dataSnapshot.children) {
                            val procedure: ProcedureModel? = procedureSnapshot.getValue(ProcedureModel::class.java)
                            if (procedure != null) {
                                binding.procedureDescriptionTb.editText?.setText(procedure.description)
                                binding.procedurePerformerTb.editText?.setText(procedure.procedurePerformer)
                                selectedDate = procedure.datePerformed
                            }
                        }
                    } else {
                        Toast.makeText(this@EditProcedureActivity,
                            "No procedure document found", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@EditProcedureActivity,
                        "${databaseError.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
    private fun populateSpinner(): ArrayAdapter<CharSequence> {
        val arrayAdapter = ArrayAdapter.createFromResource(
            this@EditProcedureActivity,
            R.array.procedureCategories,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }

    private fun deleteData(data: String) {
        val dataRef = database.getReference("procedures/$learnerId/$data")

        dataRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this@EditProcedureActivity, "Data deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@EditProcedureActivity, "Failed to delete data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, R.style.GreenDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.dateDisplayTv.text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
