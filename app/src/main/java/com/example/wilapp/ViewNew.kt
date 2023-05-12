package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button


class ViewNew : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_new)

        val BackBtn = findViewById<Button>(R.id.HomeBtn)
        BackBtn.setOnClickListener {


        val Intent = Intent(this, HomePage::class.java)
        startActivity(Intent)

    }

        val message = intent.getStringExtra("EXTRA_MESSAGE")
        val textView = findViewById<TextView>(R.id.Name).apply {

            text = message

        }



        val message2 = intent.getStringExtra("EXTRA_MESSAGE2")
        val textView2 = findViewById<TextView>(R.id.Surname).apply {

            text = message2
        }


        val message3 = intent.getStringExtra("EXTRA_MESSAGE3")
        val textView3 = findViewById<TextView>(R.id.Date).apply {

            text = message3
        }

        val message4 = intent.getStringExtra("EXTRA_MESSAGE4")
        val textView4 = findViewById<TextView>(R.id.Identity).apply {

            text = message4
        }


    }
}