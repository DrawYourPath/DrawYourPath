package com.epfl.drawyourpath.preferences

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import java.util.concurrent.CompletableFuture

class ModifyUsernameFragment : Fragment(R.layout.fragment_modify_username) {

    private var isTest: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var database: Database = FireDatabase()

        //retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if (isRunTest == null) {
            isTest = false
        } else {
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        //select the correct database in function of test scenario
        if (isTest) {
            database = MockDataBase()
        }

        //retrieve the different elements of the UI
        val testUserNameButton: Button =
            view.findViewById(R.id.test_availability_modify_username)
        val inputUserName: EditText =
            view.findViewById(R.id.input_username_modify_username)
        val errorMessageText: TextView = view.findViewById(R.id.error_text_modify_username)
        val cancelButton: Button = view.findViewById(R.id.cancel_modify_username)
        val validateButton: Button = view.findViewById(R.id.validate_modify_username)

        testUserNameButton.setOnClickListener {
            testUsernameAvailability(database, inputUserName.text.toString(), errorMessageText)
        }

        //set the username if it is correct when clicking on the validate button and return back to the preferences if it is updated
        validateButton.setOnClickListener {
            validateButtonAction(inputUserName.text.toString(), database, errorMessageText)
        }

        //return back to preferences if click on cancel button without modifying the username
        cancelButton.setOnClickListener{
            returnBackToPreviousFrag()
        }
    }
    /**
     * Helper function to test if the username can be update in the database and if if it is the case return to the preferences fragment.
     * @param username that we want to test and potentially set to the database
     * @param database used to store the user information
     * @param errorMessage text view to display the potential error message to the user
     */
    private fun validateButtonAction(username: String, database: Database, errorMessage: TextView){
        val testUsername = testUsernameAvailability(database, username, errorMessage)
        testUsername.thenAccept{available ->
            if(available){
                database.updateUsername(username).thenAccept{usernameSet ->
                    if(usernameSet){
                        returnBackToPreviousFrag()
                    }
                }
            }
        }
    }

    /**
     * Helper function to return back to the previous fragment
     */
    private fun returnBackToPreviousFrag(){
        activity?.onBackPressed()
    }

    /**
     * Helper function that display a message with outputMessage to the user on the UI and return a boolean that indicate
     * if the username is available and not equal to the previous username that user have.
     * @param username username with the availability to test
     * @param database used to test if the username is available
     * @param errorMessage editText used to show an error message to the user on the UI if the username is not available
     * @return true if the username is available or not equal to the previous one, and false otherwise
     */
    private fun testUsernameAvailability(
        database: Database,
        username: String,
        errorMessage: TextView
    ): CompletableFuture<Boolean> {
        if (username == "") {
            errorMessage.text = getString(R.string.username_can_t_be_empty)
            errorMessage.setTextColor(Color.RED)
            return CompletableFuture.completedFuture(false)
        }
        val future = database.isUsernameAvailable(username)

        val durationFuture = future.thenApply {available ->
            errorMessage.text = if(available) getString(R.string.username_available).format(username)
                else getString(R.string.username_not_vailable_or_previous_one).format(username)
            errorMessage.setTextColor(if (available) Color.GREEN else Color.RED)
            available
        }
        return durationFuture
    }
}