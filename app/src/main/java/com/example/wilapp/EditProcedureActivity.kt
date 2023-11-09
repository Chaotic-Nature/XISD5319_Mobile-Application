package com.example.wilapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityEditProcedureBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class EditProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProcedureBinding
    private lateinit var database : FirebaseDatabase
    private var selectedDate = ""
    private var learnerId = ""
    private var  procedureId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        learnerId = intent.getStringExtra("learnerId") ?: ""
        procedureId = intent.getStringExtra("procedureId") ?: ""

        database = FirebaseDatabase.getInstance()

        binding.procedureCategoryAcv.setAdapter(populateDropDown(R.array.procedureCategories))
        binding.procedurePerformerAcv.setAdapter(populateDropDown(R.array.doctors))

        retrieveData()

        binding.saveBtn.setOnClickListener {
            binding.editProcedurePb.visibility = View.VISIBLE
            binding.saveBtn.isEnabled = false

            val procedure = binding.procedureCategory.editText?.text.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString()
            val performer = binding.procedurePerformer.editText?.text.toString()
            val date = selectedDate

            if (procedure.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && performer.isNotEmpty()) {
                deleteData(procedureId) // Delete old procedure

                val newProcedureRef = database.getReference("procedures/$learnerId").push()
                newProcedureRef.setValue(ProcedureModel(procedureId, procedure, description, performer, date))
                    .addOnSuccessListener {
                        binding.editProcedurePb.visibility = View.GONE
                        binding.saveBtn.isEnabled = true
                        showMessage("Successfully edited procedure")
                        val intent = Intent(this@EditProcedureActivity, LearnerProfileActivity::class.java)
                        intent.putExtra("learner", learnerId)
                        startActivity(intent) // Start a new activity or finish() as needed
                    }
                    .addOnFailureListener {
                        binding.editProcedurePb.visibility = View.GONE
                        binding.saveBtn.isEnabled = true
                        showMessage("Failed to edit procedure: ${it.message}")
                    }
            } else {
                showMessage("Ensure that all fields are not empty")
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
        binding.editProcedurePb.visibility = View.VISIBLE
        binding.saveBtn.isEnabled = false
        database.
        getReference("procedures").child(learnerId).orderByChild(procedureId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.editProcedurePb.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    if (dataSnapshot.exists()) {
                        for (procedureSnapshot in dataSnapshot.children) {
                            val procedure: ProcedureModel? = procedureSnapshot.getValue(ProcedureModel::class.java)
                            if (procedure != null) {

                                binding.procedureDescriptionTb.editText?.setText(procedure.description)
                                binding.procedurePerformer.editText?.setText(procedure.procedurePerformer)
                                selectedDate = procedure.datePerformed
                            }
                        }
                    } else {
                        showMessage("No procedure found")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    binding.editProcedurePb.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    showMessage(databaseError.message)
                    Log.e("EDIT PROCEDURE", "Database error. ${databaseError.message}")
                }
            })
    }
    private fun populateDropDown(stringArrayId: Int): ArrayAdapter<String> {
        return ArrayAdapter(
            this@EditProcedureActivity,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(stringArrayId)
        )
    }

    private fun deleteData(data: String) {
        database.getReference("procedures/$learnerId/$data").removeValue()
            .addOnSuccessListener {
                Log.i("EDIT PROCEDURE", "Successfully deleted old procedure")
            }
            .addOnFailureListener {
                Log.e("EDIT PROCEDURE", "Failed to delete data. ${it.message}")
            }
    }

    private fun showMessage(message : String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.dateDisplayTv.text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
