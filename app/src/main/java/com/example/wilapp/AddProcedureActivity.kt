package com.example.wilapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityAddProcedureBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProcedureBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var selectedDate: Calendar
    private lateinit var selectedTime: Calendar
    private lateinit var formattedDateTime: String
    private val appointments: MutableList<Calendar> = mutableListOf()
    private lateinit var learner: String
    private lateinit var date : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        learner = intent.extras?.getString("learner").toString()
        database = FirebaseDatabase.getInstance()
        selectedDate = Calendar.getInstance()
        selectedTime = Calendar.getInstance()

        binding.dateDisplayTv.visibility = View.GONE

        binding.procedureCategoryAcv.setAdapter(populateDropDown(R.array.procedureCategories))
        binding.procedurePerformerAcv.setAdapter(populateDropDown(R.array.doctors))

        // Load existing appointments from the database
        loadAppointmentsFromProcedures()

        binding.procedureDateBtn.setOnClickListener {
            showDateTimePickerDialog()
        }

        binding.saveBtn.setOnClickListener {
            binding.addProcedurePb.visibility = View.VISIBLE
            binding.saveBtn.isEnabled = false
            val category = binding.procedureCategory.editText?.text.toString()
            val description = binding.procedureDescriptionTb.editText?.text.toString().trim()
            date = formatDate(selectedTime)
            val procedurePerformer = binding.procedurePerformer.editText?.text.toString().trim()

            if (category.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && procedurePerformer.isNotEmpty()) {
                if(!hasAppointmentConflict(selectedTime)){
                    val procedureId = database.getReference("procedures").child(learner)
                        .push()
                        .key
                    if (procedureId != null) {
                        database.getReference("procedures").child(learner)
                            .child(procedureId)
                            .setValue(ProcedureModel(procedureId, category, description, procedurePerformer, date))
                            .addOnSuccessListener {
                                binding.addProcedurePb.visibility = View.GONE
                                binding.saveBtn.isEnabled = true
                                Snackbar.make(binding.root, "Successfully added procedure",
                                    Snackbar.LENGTH_LONG).show()
                                val intent = Intent(this@AddProcedureActivity, LearnerProfileActivity::class.java)
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

                }
                else{
                    binding.addProcedurePb.visibility = View.GONE
                    binding.saveBtn.isEnabled = true


                    Snackbar.make(
                        binding.root,
                        "Appointment conflict! Please choose another time.",
                        Snackbar.LENGTH_LONG
                    ).show()

                    Handler().postDelayed({
                        showDateTimePickerDialog()
                    }, 2000)
                    date = ""
                }
            } else {
                binding.addProcedurePb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                Snackbar.make(binding.root, "Ensure that all fields are not empty.",
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
                            procedureDate.time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(procedure.datePerformed)!!
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


    private fun displaySelectedDateTime() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formattedDateTime = dateFormat.format(selectedTime.time)
        binding.dateDisplayTv.text = formattedDateTime
        binding.dateDisplayTv.visibility = View.VISIBLE
    }
}
