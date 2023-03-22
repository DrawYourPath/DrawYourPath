package com.epfl.drawyourpath.userProfileCreation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase

class UserGoalsInitFragment : Fragment(R.layout.fragment_user_goals_init) {
    private var isTest: Boolean = false
    private var username: String = ""

    //all this goals are per days
    private var timeGoal: Int = 0
    private var distanceGoal: Int = 0
    private var nunberOfPathGoal: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //retrieve the isRunTestValue and userName from the PersonalInfoFragment
        val argsFromLastFrag: Bundle? = arguments
        if (argsFromLastFrag == null) {
            isTest = false
        } else {
            isTest = argsFromLastFrag.getBoolean("isRunningTestForDataBase")
            username = argsFromLastFrag.getString("userName").toString()
        }

        //select the correct database in function of test scenario
        var database: Database? = null
        database = if (isTest) {
            MockDataBase()
        } else {
            FireDatabase()
        }
        //all the goals inputs
        val inputTimeGoal: EditText =
            view.findViewById(R.id.input_timeGoal_text_UserProfileCreation)
        val inputDistanceGoal: EditText =
            view.findViewById(R.id.input_distanceGoal_text_UserProfileCreation)
        val inputNbOfPathsGoal: EditText =
            view.findViewById(R.id.input_nbOfPathsGoal_text_UserProfileCreation)

        //all the texts where the potentials errors will be print
        val errorTextTime: TextView = view.findViewById(R.id.timeGoalError_text_userProfileCreation)
        val errorTextDistance: TextView =
            view.findViewById(R.id.distanceGoalError_text_userProfileCreation)
        val errorTextNbOfPaths: TextView =
            view.findViewById(R.id.nbOfPathsGoalError_text_userProfileCreation)

        //if all the data goals are correct that than set this data to the database associate to the username and show the next fragment,
        //when click on the validate button
        val validateButton: Button = view.findViewById(R.id.setUserGoals_button_userProfileCreation)
        validateButton.setOnClickListener {
            //check all the inputs

            val timeStr = inputTimeGoal.text.toString()
            val test1 = isNumberCorrect(timeStr, errorTextTime)
            if (test1) {
                timeGoal = Integer.parseInt(timeStr)
            }

            val distanceStr = inputDistanceGoal.text.toString()
            val test2 = isNumberCorrect(distanceStr, errorTextDistance)
            if (test2) {
                distanceGoal = Integer.parseInt(distanceStr)
            }

            val nbOfPathsStr = inputNbOfPathsGoal.text.toString()
            val test3 = isNumberCorrect(nbOfPathsStr, errorTextNbOfPaths)
            if (test3) {
                nunberOfPathGoal = Integer.parseInt(nbOfPathsStr)
            }

            if (test1 && test2 && test3) {
                database.setUserGoals(
                    username,
                    distanceGoal.toDouble(),
                    timeGoal.toDouble(),
                    nunberOfPathGoal
                )

                val previousActivity = activity
                if (previousActivity != null) {
                    val fragManagement = previousActivity.supportFragmentManager.beginTransaction()
                    val dataToEndProfileCreationFrag: Bundle = Bundle()
                    //data to transmit to the UserGoalsInitFragment(username)
                    dataToEndProfileCreationFrag.putString("userName", username)
                    val endProfileCreationFrag = EndProfileCreationFragment()
                    endProfileCreationFrag.arguments = dataToEndProfileCreationFrag
                    fragManagement.replace(R.id.userGoalInitFragment, endProfileCreationFrag)
                        .commit()
                }
            }
        }
    }
}

/**
 * Helper function to test if the number entered as input is correct. A number is correct if strctly bigger than zero and correspond to an integer.
 * If the input is empty or the input don't respect the format an error message will be printed in outputMessage
 * @param inputNumber the input string that we want to check
 * @param outputErrorText the textview where we can show error message to the user
 * @return true if the number is considered to be correct, false otherwise
 */
private fun isNumberCorrect(inputNumber: String, outputErrorText: TextView): Boolean {
    //check if the input is empty
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


    //if no error, print anything
    outputErrorText.text = ""

    return true
}