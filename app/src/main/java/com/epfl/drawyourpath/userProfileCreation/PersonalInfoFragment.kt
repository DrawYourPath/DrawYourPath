package com.epfl.drawyourpath.userProfileCreation

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import java.time.LocalDate


private val MIN_AGE: Int = 10
private val MAX_AGE: Int = 100

class PersonalInfoFragment : Fragment(R.layout.fragment_personal_info) {
    private var isTest: Boolean = false
    private var userName: String = ""
    private var dateOfBirth: LocalDate = LocalDate.now()
    private var firstname: String = ""
    private var surname: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //retrieve the isRunTestValue and userName from the UserNameTestAndSetFragment
        val argsFromLastFrag: Bundle? = arguments
        if (argsFromLastFrag == null) {
            isTest = false
        } else {
            isTest = argsFromLastFrag.getBoolean("isRunningTestForDataBase")
            userName = argsFromLastFrag.getString("userName").toString()
        }

        //select the correct database in function of test scenario
        var database: Database? = null
        database = if (isTest) {
            MockDataBase()
        } else {
            FireDatabase()
        }

        //to select a new date in a date picker
        val selectDateButton: Button = view.findViewById(R.id.selectDate_button_userProfileCreation)
        val showDateText: TextView = view.findViewById(R.id.showDate_text_userProfileCreation)

        //create a datePicker
        class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                //create a new instance of date picker
                //use the current date as the default date in the picker
                return DatePickerDialog(
                    requireContext(),
                    this,
                    LocalDate.now().year,
                    LocalDate.now().monthValue-1,
                    LocalDate.now().dayOfMonth
                )
            }

            override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                showDateText.text = buildString {
                    append(day)
                    append("/")
                    append(month + 1)
                    append("/")
                    append(year)
                }
                dateOfBirth = LocalDate.of(year, month + 1, day)
            }
        }

        //the date picker will appear if we click on the select date button
        selectDateButton.setOnClickListener {
            val datePickerFrag = DatePickerFragment()
            val originActivity = activity
            if (originActivity != null) {
                datePickerFrag.show(originActivity.supportFragmentManager, "datePicker")
            }
        }

        //all the textView where we can display errors
        val errorFirstnameText: TextView =
            view.findViewById(R.id.firstnameError_text_userProfileCreation)
        val errorSurnameText: TextView =
            view.findViewById(R.id.surnameError_text_userProfileCreation)
        val errorDateText: TextView = view.findViewById(R.id.dateError_text_userProfileCreation)

        //validate and store data if they are correct, otherwise print some error message to the user
        val validatePersonalInfoButton: Button =
            view.findViewById(R.id.setPersonalInfo_button_userProfileCreation)
        val inputFirstnameText: EditText =
            view.findViewById(R.id.input_firstname_text_UserProfileCreation)
        val inputSurnameText: EditText =
            view.findViewById(R.id.input_surname_text_UserProfileCreation)
        validatePersonalInfoButton.setOnClickListener {
            //check the firstname
            firstname = inputFirstnameText.text.toString()
            val test1 = checkName(firstname, errorFirstnameText)

            //check the surname
            surname = inputSurnameText.text.toString()
            val test2 = checkName(surname, errorSurnameText)

            val test3 = checkDateOfBirth(dateOfBirth, errorDateText)

            //if the data are correct, set them to the database and show the next fragment when click on the validate button
            if (test1 && test2 && test3) {
                database.setPersonalInfo(userName, firstname, surname, dateOfBirth)
                val previousActivity = activity
                if (previousActivity != null) {
                    val fragManagement = previousActivity.supportFragmentManager.beginTransaction()
                    val dataToUserGoalsInitFrag: Bundle = Bundle()
                    //data to transmit to the UserGoalsInitFragment(username + isTest)
                    dataToUserGoalsInitFrag.putBoolean("isRunningTestForDataBase", isTest)
                    dataToUserGoalsInitFrag.putString("userName", userName)
                    val userGoalsInitFrag = UserGoalsInitFragment()
                    userGoalsInitFrag.arguments = dataToUserGoalsInitFrag
                    fragManagement.replace(R.id.personalInfoFragment, userGoalsInitFrag).commit()
                }
            }
        }
    }
}

/**
 * Helper function to check if a name is in the correct format
 * @param name to be checked
 * @param outputErrorText text on the UI used to print the existence of error to the user
 * @return true if the name is in the correct format, false otherwise
 */
private fun checkName(name: String, outputErrorText: TextView): Boolean {
    //error messages that can be displayed in case of wrong input on click on validate
    val emptyName: String = "* This field can't be empty !"
    val incorrectName: String =
        "* This field is in an incorrect format ! It must be composed of letters or character '-'"
    //check if the name is empty
    if (name.isEmpty()) {
        outputErrorText.text = emptyName
        outputErrorText.setTextColor(Color.RED)
        return false
    }
    //check all the characters of the name
    if (name.find { !it.isLetter() && it != '-' } != null) {
        outputErrorText.text = incorrectName
        outputErrorText.setTextColor(Color.RED)
        return false
    }
    //if no error, print anything
    outputErrorText.text = ""
    return true
}

/**
 * Helper function to check if the user as the minimum required age MIN_AGE,
 * or don't have more than 100 years old with his day of birth
 * @param dateOfBirth the date of birth that we want to test
 * @param outputErrorText text on the UI used to print the existence of error to the user
 * @return true if the date of birth respect the age conditions, otherwise false
 */
private fun checkDateOfBirth(dateOfBirth: LocalDate, outputErrorText: TextView): Boolean {
    //error messages that can be displayed in case of wrong input on click on validate
    val emptyDate: String = "* You forgot to indicate your birth date"
    val incorrectDate: String = "* Your birthdate is impossible at this date"

    //The user forgot to enter a date
    if (dateOfBirth == LocalDate.now()) {
        outputErrorText.text = emptyDate
        outputErrorText.setTextColor(Color.RED)
        return false
    }

    //check if the date of birth respect the age conditions
    val todayDate = LocalDate.now()
    val minTodayAge: LocalDate =
        LocalDate.of(todayDate.year - MIN_AGE, todayDate.monthValue, todayDate.dayOfMonth)
    val maxTodayAge: LocalDate =
        LocalDate.of(todayDate.year - MAX_AGE, todayDate.monthValue, todayDate.dayOfMonth)
    if (dateOfBirth < maxTodayAge || dateOfBirth > minTodayAge) {
        outputErrorText.text = incorrectDate
        outputErrorText.setTextColor(Color.RED)
        return false
    }
    //if no error, show nothing
    outputErrorText.text = ""
    return true
}

