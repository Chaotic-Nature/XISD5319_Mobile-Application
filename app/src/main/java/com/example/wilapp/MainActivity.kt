package com.example.wilapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val LoginBtn = findViewById<Button>(R.id.LoginBtn)
        LoginBtn.setOnClickListener {

            val Intent = Intent(this, HomePage::class.java)
            startActivity(Intent)

            callActivitypoop()

        }

    }



    private fun callActivitypoop () {
        //for 1
        val UserName = findViewById<EditText>(R.id.UserName)
        val message = UserName.text.toString()



        val intent = Intent(this,HomePage::class.java).also {

            it.putExtra("EXTRA_MESSAGE",message)




            startActivity(it)
        }

    }

}