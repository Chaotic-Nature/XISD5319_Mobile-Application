package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wilapp.databinding.ActivityAddLearnerBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddLearnerBinding
    private val database = Firebase.database
    private var learnerRef = database.getReference("learners")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sexSpinner.adapter = populateSpinner()
        binding.saveBtn.setOnClickListener {
            binding.idTextField.error = null
            val id = binding.idTextField.editText?.text.toString().trim()
            val name = binding.nameTextField.editText?.text.toString().trim()
            val surname = binding.surnameTextField.editText?.text.toString().trim()
            val age = binding.ageTextField.editText?.text.toString().trim()
            val sex = binding.sexSpinner.selectedItem.toString()
            val school = binding.schoolTextField.editText?.text.toString().trim()


            if(id.isNotBlank() && name.isNotBlank() &&
                surname.isNotBlank() && age.isNotBlank() && sex.isNotBlank() && school.isNotBlank()
            )
            {
                if(id.length < 13){
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
    private fun populateSpinner(): ArrayAdapter<CharSequence> {
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sexes,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }
}
