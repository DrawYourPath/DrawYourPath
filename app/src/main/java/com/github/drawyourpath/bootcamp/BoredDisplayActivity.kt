package com.github.drawyourpath.bootcamp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.drawyourpath.bootcamp.webapi.BoredActivityModel

class BoredDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bored_display)

        val model = BoredActivityModel(this.application)

        val clearCacheButton: Button = findViewById(R.id.boredActivityClearButton)
        clearCacheButton.setOnClickListener {
            model.clearAll()
        }
        val button: Button = findViewById(R.id.boredActivityButton)
        val display: TextView = findViewById(R.id.boredActivityDisplay)
        button.setOnClickListener {
            model.getActivity(display)
        }

    }
}