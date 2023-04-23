package com.epfl.drawyourpath.userProfileCreation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import java.util.concurrent.CompletableFuture

class UserNameTestAndSetFragment : Fragment(R.layout.fragment_user_name_test_and_set) {

    private var isTest: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var database: Database = FireDatabase()

        // retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if (isRunTest == null) {
            isTest = false
        } else {
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        // select the correct database in function of test scenario
        if (isTest) {
            database = MockDataBase()
        }

        val testUserNameButton: Button =
            view.findViewById(R.id.testUserName_button_userProfileCreation)
        val inputUserName: EditText =
            view.findViewById(R.id.input_userName_text_UserProfileCreation)
        val showTestResult: TextView = view.findViewById(R.id.testUserName_text_userProfileCreation)

        testUserNameButton.setOnClickListener {
            usernameAvaibility(database, inputUserName.text.toString(), showTestResult)
        }

        val setUserNameButton: Button =
            view.findViewById(R.id.setUserName_button_userProfileCreation)
        setUserNameButton.setOnClickListener {
            // try to set the userName to the database
            val usernameStr = inputUserName.text.toString()
            val testUsername = usernameAvaibility(database, usernameStr, showTestResult)
            testUsername.thenAccept {
                showPersonalInfoFragment(activity, database, usernameStr, isTest)
            }
        }
    }
}

/**
 * Helper function to show the PersonalInfo fragment
 * @param previousActivity previous fragment activity
 * @param database used for to store the data
 * @param username that we have set into the database
 * @param isTest to know if we are in a test scenario
 */
private fun showPersonalInfoFragment(previousActivity: FragmentActivity?, database: Database, username: String, isTest: Boolean) {
    if (previousActivity != null) {
        database.setUsername(username).thenAccept {
            val fragManagement = previousActivity.supportFragmentManager.beginTransaction()
            val dataToPersoInfoFrag: Bundle = Bundle()
            // data to transmit to the PersonalInfoFragment(isTest + username)
            dataToPersoInfoFrag.putBoolean("isRunningTestForDataBase", isTest)
            dataToPersoInfoFrag.putString("username", username)
            val persoInfoFrag = PersonalInfoFragment()
            persoInfoFrag.arguments = dataToPersoInfoFrag
            fragManagement.replace(R.id.userName_frame, persoInfoFrag).commit()
        }
    }
}

/**
 * Helper function that display a message with outputMessage to the user on the UI and return a boolean that indicate
 * if the username is available
 * @param username username with the availability to tes
 * @param database used to test if the username is available
 * @param outputMessage editText used to show an error message to the user on the UI if the username is not available
 * @return true if the username is available, and false otherwise
 */
private fun usernameAvaibility(
    database: Database,
    username: String,
    outputMessage: TextView,
): CompletableFuture<Boolean> {
    if (username == "") {
        outputMessage.text = buildString { append("The username can't be empty !") }
        outputMessage.setTextColor(Color.RED)
        return CompletableFuture.completedFuture(false)
    }
    val future = database.isUsernameAvailable(username)

    // since the orTimeout require an API level 33(we are in min API level 28), we can't use it
    val durationFuture = future.thenApply {
        outputMessage.text = buildString {
            append("*The username ")
            append(username)
            append(" is ")
            append(if (!it) "NOT " else "")
            append("available !")
        }
        outputMessage.setTextColor(if (it) Color.GREEN else Color.RED)
        it
    }
    return durationFuture
}
