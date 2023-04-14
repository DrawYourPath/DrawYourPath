package com.epfl.drawyourpath.preferences

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.util.concurrent.CompletableFuture

class ModifyUsernameFragment : Fragment(R.layout.fragment_modify_username) {

    private var isTest: Boolean = false

    private val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if (isRunTest != null) {
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        //select the correct database in function of test scenario
        if (isTest) {
            user.setDatabase(MockDataBase())
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
            testUsernameAvailability(inputUserName.text.toString(), errorMessageText)
        }

        //set the username if it is correct when clicking on the validate button and return back to the preferences if it is updated
        validateButton.setOnClickListener {
            validateButtonAction(inputUserName.text.toString(), errorMessageText)
        }

        //return back to preferences if click on cancel button without modifying the username
        cancelButton.setOnClickListener {
            returnBackToPreviousFrag()
        }
    }

    /**
     * Helper function to test if the username can be update in the database and if if it is the case return to the preferences fragment.
     * @param username that we want to test and potentially set to the database
     * @param errorMessage text view to display the potential error message to the user
     */
    private fun validateButtonAction(username: String, errorMessage: TextView) {
        testUsernameAvailability(username, errorMessage).thenComposeAsync { available ->
            if (available) {
                user.setUsername(username)
            } else {
                CompletableFuture.completedFuture(false)
            }
        }.thenAcceptAsync { usernameSet ->
            if (usernameSet) {
                returnBackToPreviousFrag()
            }
        }
    }

    /**
     * Helper function to return back to the previous fragment
     */
    private fun returnBackToPreviousFrag() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    /**
     * Helper function that display a message with outputMessage to the user on the UI and return a boolean that indicate
     * if the username is available and not equal to the previous username that user have.
     * @param username username with the availability to test
     * @param errorMessage editText used to show an error message to the user on the UI if the username is not available
     * @return true if the username is available or not equal to the previous one, and false otherwise
     */
    private fun testUsernameAvailability(
        username: String,
        errorMessage: TextView
    ): CompletableFuture<Boolean> {
        if (username == "") {
            errorMessage.text = getString(R.string.username_can_t_be_empty)
            errorMessage.setTextColor(Color.RED)
            return CompletableFuture.completedFuture(false)
        }

        return user.isUsernameAvailable(username).thenApply { available ->
            errorMessage.text = if (available) getString(R.string.username_available).format(username)
            else getString(R.string.username_not_vailable_or_previous_one).format(username)
            errorMessage.setTextColor(if (available) Color.GREEN else Color.RED)
            available
        }
    }
}