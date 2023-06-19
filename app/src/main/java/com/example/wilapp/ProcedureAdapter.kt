package com.example.wilapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wilapp.databinding.ProcedureLayoutBinding

class ProcedureAdapter (private val mList: List<ProcedureModel>) :
    RecyclerView.Adapter<ProcedureAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProcedureLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val procedure = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.procedure.text = procedure.category
        holder.description.text = procedure.description
        holder.datePerformed.text = procedure.datePerformed
        holder.procedurePerformer.text = procedure.procedurePerformer


        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, procedure)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, procedure: ProcedureModel)
    }
    class ViewHolder(binding: ProcedureLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that
        // will add each item to
        val procedure : TextView = binding.procedureCategoryTv
        val description : TextView = binding.descriptionTv
        val datePerformed: TextView = binding.datePerformedTv
        val procedurePerformer: TextView = binding.performedByTv
    }
}