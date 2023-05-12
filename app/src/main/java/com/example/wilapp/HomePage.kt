package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val AddLearnBtn = findViewById<Button>(R.id.AddLearnBtn)
        AddLearnBtn.setOnClickListener {
            val Intent = Intent(this, AddNew::class.java)
            startActivity(Intent)

        }

        val message = intent.getStringExtra("EXTRA_MESSAGE")
        val textView = findViewById<TextView>(R.id.Msg).apply {

            text = message

        }


    }
}