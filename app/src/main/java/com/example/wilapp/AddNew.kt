package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

class AddNew : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)


        val BackBtn = findViewById<ImageButton>(R.id.BackBtn)
        BackBtn.setOnClickListener {


            val Intent = Intent(this, HomePage::class.java)
            startActivity(Intent)

        }


        val addBtn = findViewById<ImageButton>(R.id.AddBtn)
        addBtn.setOnClickListener{
            callActivity()
        }


    }

    private fun callActivity () {
        //for 1
        val Name = findViewById<EditText>(R.id.Name)
        val message = Name.text.toString()

        val Surname = findViewById<EditText>(R.id.Surname)
        val message2 = Surname.text.toString()

        val BirthDate = findViewById<EditText>(R.id.BirthDate)
        val message3 = BirthDate.text.toString()

        val Identity = findViewById<EditText>(R.id.Identity)
        val message4 = Identity.text.toString()

        val intent = Intent(this,ViewNew::class.java).also {

            it.putExtra("EXTRA_MESSAGE",message)

            it.putExtra("EXTRA_MESSAGE2",message2)

            it.putExtra("EXTRA_MESSAGE3",message3)

            it.putExtra("EXTRA_MESSAGE4",message4)


            startActivity(it)
        }

    }

}