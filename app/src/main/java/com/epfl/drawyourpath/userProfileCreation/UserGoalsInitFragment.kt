package com.epfl.drawyourpath.userProfileCreation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.*
import com.epfl.drawyourpath.login.launchLoginActivity
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.time.LocalDate

class UserGoalsInitFragment : Fragment(R.layout.fragment_user_goals_init) {
    private var isTest: Boolean = false
    private var username: String = ""
    private var firstname: String = ""
    private var surname: String = ""
    private var dateOfBirth: Long = 0

    // all this goals are per days
    private var timeGoal: Int = 0
    private var distanceGoal: Int = 0
    private var numberOfPathGoal: Int = 0

    private val userCached: UserModelCached by activityViewModels()

    // View elements
    lateinit var inputTimeGoal: EditText
    lateinit var inputDistanceGoal: EditText
    lateinit var inputNbOfPathsGoal: EditText
    lateinit var errorTextTime: TextView
    lateinit var errorTextDistance: TextView
    lateinit var errorTextNbOfPaths: TextView
    lateinit var validateButton: Button

    lateinit var database: Database

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // retrieve the isRunTestValue and userName from the PersonalInfoFragment
        if (arguments == null) {
            isTest = false
        } else {
            val args = requireArguments()
            isTest = args.getBoolean(PROFILE_TEST_KEY)
            username = args.getString(PROFILE_USERNAME_KEY).toString()
            firstname = args.getString(PROFILE_FIRSTNAME_KEY).toString()
            surname = args.getString(PROFILE_SURNAME_KEY).toString()
            dateOfBirth = args.getLong(PROFILE_BIRTHDATE_KEY)
        }

        // select the correct database in function of test scenario
        database = createDatabase(isTest)

        // all the goals inputs
        inputTimeGoal = view.findViewById(R.id.input_timeGoal_text_UserProfileCreation)
        inputDistanceGoal = view.findViewById(R.id.input_distanceGoal_text_UserProfileCreation)
        inputNbOfPathsGoal = view.findViewById(R.id.input_nbOfPathsGoal_text_UserProfileCreation)

        // all the texts where the potentials errors will be print
        errorTextTime = view.findViewById(R.id.timeGoalError_text_userProfileCreation)
        errorTextDistance = view.findViewById(R.id.distanceGoalError_text_userProfileCreation)
        errorTextNbOfPaths = view.findViewById(R.id.nbOfPathsGoalError_text_userProfileCreation)

        // if all the data goals are correct that than set this data to the database associate to the username and show the next fragment,
        // when click on the validate button
        validateButton = view.findViewById(R.id.setUserGoals_button_userProfileCreation)
        validateButton.setOnClickListener { onValidateButtonClicked() }
    }

    private fun createDatabase(isTest: Boolean): Database {
        return if (isTest) {
            userCached.setDatabase(MockDatabase())
            MockDatabase()
        } else {
            FirebaseDatabase()
        }
    }

    private fun isTimeGoalValid(): Boolean {
        val timeStr = inputTimeGoal.text.toString()
        val isValid = isNumberCorrect(timeStr, errorTextTime)
        if (isValid) {
            timeGoal = Integer.parseInt(timeStr)
        }
        return isValid
    }

    private fun isDistanceValid(): Boolean {
        val distanceStr = inputDistanceGoal.text.toString()
        val isValid = isNumberCorrect(distanceStr, errorTextDistance)
        if (isValid) {
            distanceGoal = Integer.parseInt(distanceStr)
        }
        return isValid
    }

    private fun isPathCountValid(): Boolean {
        val nbOfPathsStr = inputNbOfPathsGoal.text.toString()
        val isValid = isNumberCorrect(nbOfPathsStr, errorTextNbOfPaths)
        if (isValid) {
            numberOfPathGoal = Integer.parseInt(nbOfPathsStr)
        }
        return isValid
    }

    private fun getUser(): User? {
        return if (isTest) {
            MockAuth.MOCK_USER
        } else {
            FirebaseAuth.getUser()
        }
    }

    private fun onValidateButtonClicked() {
        val timeGoalValid = isTimeGoalValid()
        val distanceValid = isDistanceValid()
        val pathCountValid = isPathCountValid()

        if (timeGoalValid && distanceValid && pathCountValid) {
            val user = getUser()
            if (user == null) {
                launchLoginActivity(requireActivity())
                return
            }

            userCached.setDatabase(database)

            // TODO: Remove user model
            val usermodel = UserModel(
                user,
                username,
                firstname,
                surname,
                LocalDate.ofEpochDay(dateOfBirth),
                distanceGoal.toDouble(),
                timeGoal.toDouble(),
                numberOfPathGoal,
                database,
            )

            userCached.createNewUser(usermodel)

            database.createUser(user.getUid(), UserData(
                username = username,
                firstname = firstname,
                birthDate = dateOfBirth,
                goals = UserGoals(
                    distance = distanceGoal.toDouble(),
                    activityTime = timeGoal.toLong(),
                    paths = numberOfPathGoal.toLong(),
                )
            )).thenAccept {
                if (activity != null) {
                    launchNextFragment()
                }
            }
        }
    }

    private fun launchNextFragment() {
        val fragManagement = requireActivity().supportFragmentManager.beginTransaction()
        val dataToPhotoProfileInitFrag: Bundle = Bundle()

        val photoProfileFrag = PhotoProfileInitFragment()
        photoProfileFrag.arguments = bundleOf(
            // TODO: extract the key.
            PROFILE_TEST_KEY to isTest,

            // TODO: Don't use database key in a unrelated context.
            // PROFILE_USERNAME_KEY to username,
        )

        fragManagement.replace(
            R.id.userGoalInitFragment,
            photoProfileFrag,
        ).commit()
    }

    /**
     * Helper function to test if the number entered as input is correct. A number is correct if strctly bigger than zero and correspond to an integer.
     * If the input is empty or the input don't respect the format an error message will be printed in outputMessage
     * @param inputNumber the input string that we want to check
     * @param outputErrorText the textview where we can show error message to the user
     * @return true if the number is considered to be correct, false otherwise
     */
    private fun isNumberCorrect(inputNumber: String, outputErrorText: TextView): Boolean {
        // check if the input is empty
        if (inputNumber == "") {
            outputErrorText.text = "* This field can't be empty !"
            outputErrorText.setTextColor(Color.RED)
            return false
        }

        if (inputNumber.find { !(it.isDigit()) } != null) {
            outputErrorText.text = "* This field can be only composed of integer number."
            outputErrorText.setTextColor(Color.RED)
            return false
        }

        // if no error, print anything
        outputErrorText.text = ""

        return true
    }
}

