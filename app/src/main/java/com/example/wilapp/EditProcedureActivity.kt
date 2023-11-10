package com.example.wilapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wilapp.databinding.ActivityEditProcedureBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProcedureBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var selectedDate: Calendar
    private lateinit var selectedTime: Calendar
    private lateinit var formattedDateTime: String
    private val appointments: MutableList<Calendar> = mutableListOf()
    private var learner = ""
    private var procedureId = ""
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        learner = intent.getStringExtra("learnerId") ?: ""
        procedureId = intent.getStringExtra("procedureId") ?: ""

        database = FirebaseDatabase.getInstance()
        selectedDate = Calendar.getInstance()
        selectedTime = Calendar.getInstance()
        loadAppointmentsFromProcedures()
        binding.procedureCategoryAcv.setAdapter(populateDropDown(R.array.procedureCategories))
        binding.procedurePerformerAcv.setAdapter(populateDropDown(R.array.doctors))

        retrieveData()

        binding.saveBtn.setOnClickListener {
            binding.editProcedurePb.visibility = View.VISIBLE
            binding.saveBtn.isEnabled = false

            val procedure = binding.procedureCategory.editText?.text.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString()
            val performer = binding.procedurePerformer.editText?.text.toString()
            date = binding.dateDisplayTv.text.toString()

            if (procedure.isNotEmpty() && description.isNotEmpty() && performer.isNotEmpty()) {
                if (!hasAppointmentConflict(selectedTime)) {
                    deleteData(procedureId) // Delete old procedure

                    val newProcedureRef = database.getReference("procedures/$learner").push()
                    newProcedureRef.setValue(ProcedureModel(procedureId, procedure, description, performer, date))
                        .addOnSuccessListener {
                            binding.editProcedurePb.visibility = View.GONE
                            binding.saveBtn.isEnabled = true
                            showMessage("Successfully edited procedure")
                            val intent = Intent(this@EditProcedureActivity, LearnerProfileActivity::class.java)
                            intent.putExtra("learner", learner)
                            startActivity(intent) // Start a new activity or finish() as needed
                        }
                        .addOnFailureListener {
                            binding.editProcedurePb.visibility = View.GONE
                            binding.saveBtn.isEnabled = true
                            showMessage("Failed to edit procedure: ${it.message}")
                        }
                } else {
                    handleAppointmentConflict()
                }
            } else {
                binding.editProcedurePb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                showMessage("Ensure that all fields are not empty")
            }
        }

        binding.procedureDateBtn.setOnClickListener {
            showDateTimePickerDialog()

        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun retrieveData() {
        binding.editProcedurePb.visibility = View.VISIBLE
        binding.saveBtn.isEnabled = false
        database.reference
            .child("procedures")
            .child(learner)
            .child(procedureId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.editProcedurePb.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    if (dataSnapshot.exists()) {
                        val procedure: ProcedureModel? = dataSnapshot.getValue(ProcedureModel::class.java)
                        if (procedure != null) {
                            val categoryArray = resources.getStringArray(R.array.procedureCategories)
                            val performerArray = resources.getStringArray(R.array.doctors)

                            // Set the adapter for the AutoCompleteTextView
                            binding.procedureCategoryAcv.setAdapter(ArrayAdapter(this@EditProcedureActivity, android.R.layout.simple_dropdown_item_1line, categoryArray))
                            binding.procedurePerformerAcv.setAdapter(ArrayAdapter(this@EditProcedureActivity, android.R.layout.simple_dropdown_item_1line, performerArray))

                            // Set the selected item based on the procedure data
                            binding.procedureCategoryAcv.setText(procedure.category, false)
                            binding.procedureDescriptionTb.editText?.setText(procedure.description)
                            binding.procedurePerformerAcv.setText(procedure.procedurePerformer, false)
                            date = procedure.datePerformed
                            binding.dateDisplayTv.text = procedure.datePerformed
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
        database.getReference("procedures/$learner/$data").removeValue()
            .addOnSuccessListener {
                Log.i("EDIT PROCEDURE", "Successfully deleted old procedure")
            }
            .addOnFailureListener {
                Log.e("EDIT PROCEDURE", "Failed to delete data. ${it.message}")
            }
    }

    private fun showDateTimePickerDialog() {
        // Date picker dialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                // Update selectedDate with the chosen date
                selectedDate.set(year, month, day)

                // Time picker dialog
                showTimePickerDialog()

            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Update selectedTime with the chosen time
                selectedTime.set(
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute
                )
                binding.dateDisplayTv.text = formatDate(selectedTime)
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            false // 24-hour time format
        )

        // Set the time range from 8am to 3pm
        timePickerDialog.updateTime(8, 0)

        // Show the time picker dialog
        timePickerDialog.show()
    }

    // Function to format a Calendar object as a string
    private fun formatDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun loadAppointmentsFromProcedures() {
        database.reference.child("procedures").child(learner)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    appointments.clear() // Clear existing appointments
                    for (pulledProcedure in dataSnapshot.children) {
                        val procedure: ProcedureModel? =
                            pulledProcedure.getValue(ProcedureModel::class.java)

                        if (procedure != null) {
                            val procedureDate = Calendar.getInstance()
                            procedureDate.time =
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(procedure.datePerformed)!!
                            appointments.add(procedureDate)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

    private fun hasAppointmentConflict(newAppointment: Calendar): Boolean {
        // Convert the new appointment to a formatted string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val newAppointmentString = dateFormat.format(newAppointment.time)

        // Check for conflicts with existing appointments
        for (existingAppointment in appointments) {
            // Convert the existing appointment to a formatted string
            val existingAppointmentString = dateFormat.format(existingAppointment.time)

            // Check if the new appointment clashes with an existing appointment
            if (newAppointmentString == existingAppointmentString) {
                return true
            }
        }
        // No conflicts
        return false
    }

    private fun handleAppointmentConflict() {
        binding.editProcedurePb.visibility = View.GONE
        binding.saveBtn.isEnabled = true

        Snackbar.make(
            binding.root,
            "Appointment conflict! Please choose another time.",
            Snackbar.LENGTH_LONG
        ).show()

        // Use a coroutine to introduce a delay before showing the date time picker
        lifecycleScope.launch {
            delay(2000)
            showDateTimePickerDialog()
        }
        date = ""
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
