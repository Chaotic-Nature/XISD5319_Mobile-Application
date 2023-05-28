package com.example.wilapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wilapp.databinding.ActivityAddLearnerBinding

class AddLearnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddLearnerBinding
    //private val database = Firebase.database
    //private var wellnessMobileClinicRef = database.getReference("learners")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.saveBtn.setOnClickListener {
            val id = binding.idTextField.editText?.text.toString().trim()
            val name = binding.nameTextField.editText?.text.toString().trim()
            val surname = binding.surnameTextField.editText?.text.toString().trim()
            val age = binding.ageTextField.editText?.text.toString().trim()
            val sex = binding.sexTextField.editText?.text.toString().trim()
            val school = binding.schoolTextField.editText?.text.toString().trim()

/*  if(!id.isNullOrBlank() && !name.isNullOrBlank() &&
      !surname.isNullOrBlank() && !age.isNullOrBlank() && !sex.isNullOrBlank() && !school.isNullOrBlank()){

      wellnessMobileCnicRef.push()
          .setValue(LearnerModel(id, name, surname, age.toInt(), sex, school))
          .addOnSuccessListener{
              Toast.makeText(this, "Successfully added learner", Toast.LENGTH_LONG).show()
          }
          .addOnFailureListener {e ->
              Toast.makeText(this, "Something went wrong. $e", Toast.LENGTH_LONG).show()
          }
  }
  else{
      Toast.makeText(this, "fields cannot be empty.",
          Toast.LENGTH_SHORT).show()
  } */
}
}
}