package com.example.wilapp

import android.os.Bundle
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

class EditProcedureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProcedureBinding
    private val database = Firebase.database
    private lateinit var dataList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProcedureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataList = mutableListOf()

        binding.deleteBtn.setOnClickListener {
            val selectedItem = binding.dataSpinner.selectedItem as String
            deleteData(selectedItem)
        }

        retrieveData()
    }

    private fun retrieveData() {
        val learnerId = intent.extras?.getString("learner")
        val dataRef = database.getReference("procedures/$learnerId")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList.clear()
                for (dataSnapshotChild in dataSnapshot.children) {
                    val data = dataSnapshotChild.getValue(ProcedureModel::class.java)

                    if (data != null) {
                        dataList.add(data.category)
                    }
                }
                populateSpinner()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EditProcedureActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        }
        dataRef.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun populateSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dataSpinner.adapter = adapter
    }

    private fun deleteData(data: String) {
        val learnerId = intent.extras?.getString("learner")
        val dataRef = database.getReference("procedures/$learnerId")

        val dataQuery = dataRef.orderByChild("category").equalTo(data)
        dataQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshotChild in dataSnapshot.children) {
                    dataSnapshotChild.ref.removeValue()
                }
                Toast.makeText(this@EditProcedureActivity, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                retrieveData()
                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EditProcedureActivity, "Failed to delete data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
