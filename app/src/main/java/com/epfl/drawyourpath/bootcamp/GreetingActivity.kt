package com.epfl.drawyourpath.bootcamp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.epfl.drawyourpath.R

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        val printText: TextView = findViewById(R.id.greetingMessage)
        printText.text = buildString {
            append("Hello ")
            append(intent.getStringExtra("userName"))
            append(" !")
        }
    }
}