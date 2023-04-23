package com.epfl.drawyourpath.userProfileCreation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.Auth
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.database.MockDatabase
import java.util.concurrent.CompletableFuture

class SelectUsernameFragment : Fragment(R.layout.fragment_user_name_test_and_set) {

    private var isTest: Boolean = false

    private lateinit var database: Database
    private lateinit var auth: Auth

    private lateinit var outputView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // retrieve the value from the welcome activity to know if we are running testes
        isTest = when (arguments) {
            null -> false
            else -> requireArguments().getBoolean(PROFILE_TEST_KEY)
        }

        setupEnv(isTest)

        // select the correct database in function of test scenario
        val testUserNameButton: Button =
            view.findViewById(R.id.testUserName_button_userProfileCreation)
        val inputUserName: EditText =
            view.findViewById(R.id.input_userName_text_UserProfileCreation)

        outputView = view.findViewById(R.id.testUserName_text_userProfileCreation)

        testUserNameButton.setOnClickListener {
            checkAvailability(inputUserName.text.toString())
        }

        val setUserNameButton: Button =
            view.findViewById(R.id.setUserName_button_userProfileCreation)
        setUserNameButton.setOnClickListener {
            // try to set the userName to the database
            val usernameStr = inputUserName.text.toString()

            Log.i("DYP", "Checking availability of username $usernameStr")

            checkAvailability(usernameStr).thenAccept {
                if (it) {
                    showPersonalInfoFragment(usernameStr)
                    Log.i("DYP", "Username $usernameStr is available")
                }
                else {
                    Log.e("DYP", "Username $usernameStr is taken")
                }
            }.exceptionally {
                Log.e("DYP", "Failed to check username: ${it.message}" )
                it.printStackTrace()
                setErrorMessage("${it.message}")
                null
            }
        }
    }

    private fun setupEnv(isTest: Boolean) {
        when (isTest) {
            true -> {
                Log.i("DYP", "SelectUsername fragment uses mock API.")

                database = MockDatabase()
                auth = MockAuth(forceSigned = true)
            }
            false -> {
                database = FirebaseDatabase()
                auth = MockAuth()
            }
        }
    }

    private fun setErrorMessage(error: String) {
        outputView.text = error
        outputView.setTextColor(Color.RED)
    }

    private fun setSuccessMessage() {
        outputView.text = "The username is available."
        outputView.setTextColor(Color.GREEN)
    }

    /**
     * Helper function that display a message with outputMessage to the user on the UI and return a boolean that indicate
     * if the username is available
     * @param username username with the availability to tes
     * @param database used to test if the username is available
     * @param outputMessage editText used to show an error message to the user on the UI if the username is not available
     * @return true if the username is available, and false otherwise
     */
    private fun checkAvailability(username: String): CompletableFuture<Boolean> {
        if (username.isEmpty()) {
            setErrorMessage("The username can't be empty !")
            return CompletableFuture.completedFuture(false)
        }

        val result = CompletableFuture<Boolean>()

        database.isUsernameAvailable(username).thenAccept {
            if (!it) {
                setErrorMessage("The username is already taken.")
                result.complete(false)
                return@thenAccept
            }

            setSuccessMessage()
            result.complete(true)
        }.exceptionally {
            setErrorMessage("Failed with error: ${it.localizedMessage}")
            result.complete(false)
            null
        }

        return result
    }

    /**
     * Helper function to show the PersonalInfo fragment
     * @param username that we have set into the database
     */
    private fun showPersonalInfoFragment(username: String) {
        if (activity != null) {
            val uid = auth.getUser()!!.getUid()

            Log.i("DYP", "Setting the username.")

            database.setUsername(uid, username).thenAccept {
                val fragManagement = requireActivity().supportFragmentManager.beginTransaction()
                val dataToPersoInfoFrag: Bundle = Bundle()
                // data to transmit to the PersonalInfoFragment(isTest + username)
                dataToPersoInfoFrag.putBoolean(PROFILE_TEST_KEY, isTest)
                dataToPersoInfoFrag.putString("username", username)
                val persoInfoFrag = PersonalInfoFragment()
                persoInfoFrag.arguments = dataToPersoInfoFrag
                fragManagement.replace(R.id.userName_frame, persoInfoFrag).commit()

                Log.i("DYP", "Moved to next fragment.")

            }.exceptionally {
                Log.e("DYP", "Failed to set username: ${it.message}")
                it.printStackTrace()

                setErrorMessage(it.message ?: "Unknown error")
                null
            }
        }
        else {
            Log.e("DYP", "Can't move to next fragment as activity is null.")
        }
    }
}


