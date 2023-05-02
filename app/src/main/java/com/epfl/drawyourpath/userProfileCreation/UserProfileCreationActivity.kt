package com.epfl.drawyourpath.userProfileCreation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.epfl.drawyourpath.R

const val PROFILE_TEST_KEY = "isRunningTestForDataBase2"
const val PROFILE_USERNAME_KEY = "username"
const val PROFILE_FIRSTNAME_KEY = "firstname"
const val PROFILE_SURNAME_KEY = "surname"
const val PROFILE_BIRTHDATE_KEY = "birthdate"

class UserProfileCreationActivity : AppCompatActivity(R.layout.activity_user_profile_creation) {

    var isTest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isTest = intent.extras?.getBoolean(PROFILE_TEST_KEY) ?: false

        val beginButton: Button =
            findViewById(R.id.start_profile_creation_button_userProfileCreation)
        beginButton.setOnClickListener { launchNextFragment() }
    }

    private fun launchNextFragment() {
        val fragUserNameFragTransaction = supportFragmentManager.beginTransaction()
        val dataToUserNameFrag = Bundle()
        dataToUserNameFrag.putBoolean(PROFILE_TEST_KEY, isTest)
        // inform the UserNameTestAndSet fragment if we are in a test scenario
        val userNameFrag = SelectUsernameFragment()
        userNameFrag.arguments = dataToUserNameFrag
        fragUserNameFragTransaction.replace(R.id.userProfileStartFrame, userNameFrag).commit()
    }
}
