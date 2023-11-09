package com.example.wilapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.wilapp.databinding.ActivityAddLearnerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Date

class AddLearnerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLearnerBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()

        /*Creating an adapter to populate the schools drop down list with a string array of schools*/
        val adapter = ArrayAdapter(this@AddLearnerActivity,
            android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.schools)
        )
        binding.schoolsDropDownAcv.setAdapter(adapter)

        binding.saveBtn.setOnClickListener {
            /*Setting all input fields error indicators to null.*/
            binding.idTextField.error = null
            binding.nameTextField.error = null
            binding.surnameTextField.error = null

            /*Getting user input*/
            val id = binding.idTextField.editText?.text.toString().trim()
            val name = binding.nameTextField.editText?.text.toString().trim()
            val surname = binding.surnameTextField.editText?.text.toString().trim()
            val school = binding.schoolsDropDown.editText?.text.toString()
            val age = calculateAgeFromIdNumber(id)
            val sex = determineSexFromIdNumber(id)
            /*Regular expression is used to validate the ID as a S.A ID*/
            val saIdRegex = """^\d{13}$""".toRegex()

            /*Displaying error messages to the relevant textBox*/
            if (name.isEmpty()) {
                binding.idTextField.error = "Please enter a name"
            } else if (surname.isEmpty()) {
                binding.idTextField.error = "Please enter a surname"
            } else if (id.isEmpty()) {
                binding.idTextField.error = "Please enter an ID number"
            } else if (!id.matches(saIdRegex)) {
                binding.idTextField.error = "Must be valid ID number"
            }
            else {
                binding.addLearnerPb.visibility = View.VISIBLE //Makes loading icon visible.
                binding.saveBtn.isEnabled = false//Disables save button.

                if (learnerExists(id)){
                    showMessage("Learner already exists")
                }else{
                    addLearner(LearnerModel(id,name,surname,age.toInt(),sex,school))
                }
            }
        }
    }

    private fun addLearner(learner : LearnerModel){
        database.getReference("learners").child(learner.id)
            .setValue(learner)
            .addOnSuccessListener {
                binding.addLearnerPb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                showMessage("Successfully added learner")
                finish()
            }
            .addOnFailureListener { e ->
                binding.addLearnerPb.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                showMessage("Something went wrong")
                Log.e("ADD LEARNER ACTIVITY", "Something went wrong: ${e.message}")
            }
    }

    private fun learnerExists(learnerId  :String) : Boolean{
        var learnerExists = false
        database.getReference("learners").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (pulledLearner in snapshot.children) {
                    val learner: LearnerModel? = pulledLearner.getValue(LearnerModel::class.java)

                    if (learner == null && pulledLearner.key != learnerId) {
                        learnerExists = false
                    }
                    else{
                        learnerExists = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                learnerExists = true
                showMessage("Something went wrong")
                Log.e("ADD LEARNER ACTIVITY", "Something went wrong: ${error.message}")
            }
        })
        binding.addLearnerPb.visibility = View.GONE
        binding.saveBtn.isEnabled = true
        return learnerExists
    }

    /*This code determines the users sex based on their id number*/
    private fun determineSexFromIdNumber(idNumber: String): String {
        val sex: String
        val userSex = idNumber.substring(7).toInt()
        /*if its greater or equal to 0 and less than or equal to 4 then the user is female*/
        sex = if (userSex in 0..4) {
            "Female"
        } else {
            "Male"
        }
        return sex
    }

    /*This code determines the users age based on their id number*/
    private fun calculateAgeFromIdNumber(idNumber: String): String {
        // Extract year, month, and day from the ID number
        val yearStr = idNumber.substring(0, 2)
        val monthStr = idNumber.substring(2, 4)
        val dayStr = idNumber.substring(4, 6)

        // Current date
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Month is zero-based
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Convert extracted substrings to integers
        val birthYear = yearStr.toInt() + 2000 // Assuming 2000 as the base year
        val birthMonth = monthStr.toInt()
        val birthDay = dayStr.toInt()

        // Calculate age
        var age = currentYear - birthYear
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age-- // Subtract 1 year if the birthday hasn't occurred yet this year
        }

        return age.toString()
    }

    private fun showMessage(message : String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
