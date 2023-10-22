package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wilapp.databinding.ActivityAddLearnerBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date

class AddLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddLearnerBinding
    private val database = Firebase.database
    private var learnerRef = database.getReference("learners")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.schoolsSpinner.adapter = populateSpinner(R.array.schools)

        binding.saveBtn.setOnClickListener {
            binding.idTextField.error = null
            val id = binding.idTextField.editText?.text.toString().trim()
            val name = binding.nameTextField.editText?.text.toString().trim()
            val surname = binding.surnameTextField.editText?.text.toString().trim()
            val age = calculateAgeFromIdNumber(id)
            val sex = determineSexFromIdNumber(id)
            val school = binding.schoolsSpinner.selectedItem.toString()
            val saIdRegex = """^\d{13}$""".toRegex()


            if(id.isNotBlank() && name.isNotBlank() &&
                surname.isNotBlank() && age.isNotBlank() && sex.isNotBlank() && school.isNotBlank()
            )
            {
                if(!id.matches(saIdRegex)){
                    binding.idTextField.error = "Must be valid ID number"
                }
                else{
                    learnerRef.child(id)
                        .setValue(LearnerModel(id, name, surname, age.toInt(), sex, school))
                        .addOnSuccessListener{
                            Toast.makeText(this, "Successfully added learner", Toast.LENGTH_LONG).show()
                            finish()
                        }
                        .addOnFailureListener {e ->
                            Toast.makeText(this, "Something went wrong. $e", Toast.LENGTH_LONG).show()
                        }
                }
            }
            else{
                Toast.makeText(this, "Fields cannot be empty.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun populateSpinner(arrayResourceId: Int): ArrayAdapter<CharSequence> {
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            arrayResourceId,
            R.layout.spinner_list
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }


    /*This code determines the users sex based on their id number*/
    private fun determineSexFromIdNumber(idNumber: String) : String{
        val sex: String
        val userSex = idNumber.substring(7).toInt()

        sex = if(userSex in 0..4){ /*if its greater or equal to 0 and less than or equal to 4 then the user is female*/
                    "Female"
              }
        else{
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
}
