package com.example.wilapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wilapp.databinding.ActivityLearnerProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LearnerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLearnerProfileBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val learnerId = intent.getStringExtra("learner") ?: ""
        database = FirebaseDatabase.getInstance()
        binding.noScreeningTv.visibility = View.VISIBLE
        binding.screeningResultsTv.visibility = View.GONE

        loadLearnerInformation(learnerId)
        loadScreeningResults(learnerId)
        loadProcedures(learnerId)
        setupClickListeners(learnerId)
    }

    private fun loadLearnerInformation(learnerId: String) {
        binding.profilePb.visibility = View.VISIBLE
        var learnerName: String
        database.reference.child("learners")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (pulledLearner in snapshot.children) {
                        val learner: LearnerModel? =
                            pulledLearner.getValue(LearnerModel::class.java)
                        if (learner != null && pulledLearner.key == learnerId) {
                            learnerName = "${learner.name} ${learner.surname}"
                            binding.nameTv.text = learnerName
                            binding.ageTv.text = learner.age.toString()
                            binding.sexTv.text = learner.sex
                            binding.idTv.text = learner.id
                            binding.schoolTv.text = learner.school
                            binding.profilePb.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.profilePb.visibility = View.GONE
                    showSnackbar("Failed to read value ${error.toException()}")
                }
            })
    }

    private fun loadScreeningResults(learnerId: String) {
        // Load screening results from the database and update UI
        binding.profilePb.visibility = View.VISIBLE
        database.reference.child("screeningQuestions/$learnerId")
            .addValueEventListener(object : ValueEventListener {
                var displayText = ""
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (pulledQuestion in dataSnapshot.children) {
                        val q: ScreeningQuestionsModel? =
                            pulledQuestion.getValue(ScreeningQuestionsModel::class.java)

                        if (q != null) {
                            displayText += "Q: ${q.question}\nA: ${q.answer}\n\n"
                            binding.addScreeningBtn.isEnabled = false
                            binding.noScreeningTv.visibility = View.GONE
                            binding.profilePb.visibility = View.GONE
                            binding.screeningResultsTv.visibility = View.VISIBLE
                            binding.addScreeningBtn
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@LearnerProfileActivity,
                                        R.color.grey
                                    )
                                )

                        }
                    }
                    binding.screeningResultsTv.text = displayText
                }

                override fun onCancelled(error: DatabaseError) {
                    showSnackbar("Error displaying learner screening information")
                    binding.profilePb.visibility = View.GONE
                }
            })
    }

    private fun loadProcedures(learnerId: String) {
        // Load procedures from the database and update UI
        binding.profilePb.visibility = View.VISIBLE
        database.reference.child("procedures/$learnerId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val displayText = "No procedure record available."
                    val procedureList = mutableListOf<ProcedureModel>()
                    for (pulledQuestion in dataSnapshot.children) {
                        val procedure: ProcedureModel? =
                            pulledQuestion.getValue(ProcedureModel::class.java)

                        if (procedure != null) {
                            val procedureKey = pulledQuestion.key
                            if (procedureKey != null) {
                                procedure.id = procedureKey
                                procedureList.add(procedure)
                            }

                            binding.noProcedureTv.visibility = View.GONE
                        } else {
                            binding.noProcedureTv.text = displayText
                        }
                    }

                    val adapter = ProcedureAdapter(procedureList)
                    binding.procedureRv.layoutManager =
                        LinearLayoutManager(this@LearnerProfileActivity)
                    binding.procedureRv.adapter = adapter

                    adapter.setOnClickListener(object : ProcedureAdapter.OnClickListener {
                        override fun onClick(position: Int, procedure: ProcedureModel) {
                            intent = Intent(
                                this@LearnerProfileActivity,
                                EditProcedureActivity::class.java
                            )
                            intent.putExtra("procedureId", procedure.id)
                            intent.putExtra("learnerId", learnerId)
                            startActivity(intent)
                        }
                    })
                    binding.profilePb.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.profilePb.visibility = View.GONE
                    showSnackbar("Error displaying learner procedure information")
                }
            })
    }

    private fun setupClickListeners(learnerId: String) {
        // Set up click listeners for buttons
        binding.editScreeningBtn.setOnClickListener {
            intent = Intent(this, EditScreeningActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
            finish()
        }

        binding.addScreeningBtn.setOnClickListener {
            intent = Intent(this, AddScreeningActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
            finish()
        }

        binding.addProcedureBtn.setOnClickListener {
            intent = Intent(this, AddProcedureActivity::class.java)
            intent.putExtra("learner", learnerId)
            startActivity(intent)
            finish()
        }

        binding.returnBtn.setOnClickListener {
            finish()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}