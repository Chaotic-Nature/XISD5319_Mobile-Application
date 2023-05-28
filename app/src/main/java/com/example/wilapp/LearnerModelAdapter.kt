package com.example.wilapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wilapp.databinding.LearnerInfoCardLayoutBinding

class LearnerModelAdapter(private val mList: List<LearnerModel>) :
    RecyclerView.Adapter<LearnerModelAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LearnerInfoCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val learner = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.name.text = learner.name
        holder.age.text = learner.age.toString()
        holder.id.text = learner.id

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, learner)
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
        fun onClick(position: Int, learner: LearnerModel)
    }
    class ViewHolder(binding: LearnerInfoCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that
        // will add each item to
        val name : TextView = binding.nameText
        val age : TextView = binding.ageText
        val id: TextView = binding.idText
    }
}
