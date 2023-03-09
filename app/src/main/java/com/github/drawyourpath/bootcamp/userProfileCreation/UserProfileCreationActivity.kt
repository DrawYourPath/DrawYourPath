package com.github.drawyourpath.bootcamp.userProfileCreation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.database.Database
import com.github.drawyourpath.bootcamp.database.FireDatabase
import com.github.drawyourpath.bootcamp.database.MockDataBase


class UserProfileCreationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_creation)

        //test if we are running tests in the dataBase
        val dataBase: Database
        val isTest: Boolean = intent.extras?.getBoolean("isRunningTestForDataBase") ?: false
        if (isTest) {
            dataBase = MockDataBase()
        } else {
            dataBase = FireDatabase()
        }

        val beginButton: Button =
            findViewById(R.id.start_profile_creation_button_userProfileCreation)
        beginButton.setOnClickListener {
            val fragUserNameFragTransaction = supportFragmentManager.beginTransaction()
            val dataToUserNameFrag: Bundle = Bundle()
            //inform the UserNameTestAndSet fragment if we are in a test scenario
            dataToUserNameFrag.putBoolean("isRunningTestForDataBase", isTest)
            val userNameFrag = UserNameTestAndSetFragment()
            userNameFrag.arguments = dataToUserNameFrag
            fragUserNameFragTransaction.replace(R.id.userProfileStartFrame, userNameFrag).commit()
        }
    }
}

