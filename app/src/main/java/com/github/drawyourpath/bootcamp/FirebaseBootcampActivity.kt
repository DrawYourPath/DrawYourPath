package com.github.drawyourpath.bootcamp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class FirebaseBootcampActivity : AppCompatActivity() {

    var dataBase: Database = FireDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_bootcamp)

        //test if we are running tests in the dataBase
        val isTest: Boolean = intent.extras?.getBoolean("isRunningTestForDataBase") ?: false
        if (isTest) {
            dataBase = MockDataBase()
        }

        val mailInput: EditText = findViewById(R.id.textMailFirebase)
        val phoneInput: EditText = findViewById(R.id.textPhoneFirebase)

        val setButton: Button = findViewById(R.id.setButtonFirebase)
        setButton.setOnClickListener { dataBase.set(mailInput, phoneInput) }

        val getButton: Button = findViewById(R.id.getButtonFirebase)
        getButton.setOnClickListener { dataBase.get(mailInput, phoneInput) }
    }

}