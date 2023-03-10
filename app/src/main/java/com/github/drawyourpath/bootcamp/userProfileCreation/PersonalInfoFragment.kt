package com.github.drawyourpath.bootcamp.userProfileCreation

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.database.Database
import com.github.drawyourpath.bootcamp.database.FireDatabase
import com.github.drawyourpath.bootcamp.database.MockDataBase
import java.util.*


private val MIN_AGE: Int = 10
private val MAX_AGE: Int = 100

class PersonalInfoFragment : Fragment() {
    private var isTest: Boolean = false
    private var userName: String = ""
    private var dayBirth: Int = 0
    private var monthBirth: Int = 0
    private var yearBirth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_personal_info, container, false)

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
        if (isTest) {
            database = MockDataBase()
        } else {
            database = FireDatabase()
        }

        //to select a new date in a date picker
        val selectDateButton: Button = view.findViewById(R.id.selectDate_button_userProfileCreation)
        val showDateText: TextView = view.findViewById(R.id.showDate_text_userProfileCreation)

        //Today mounth, day, year
        val c = Calendar.getInstance()
        val todayD = c.get(Calendar.DAY_OF_MONTH)
        val todayM = c.get(Calendar.MONTH)
        val todayY = c.get(Calendar.YEAR)

        //create a datePicker
        class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                //create a new instance of date picker
                //use the current date as the default date in the picker
                return DatePickerDialog(requireContext(), this, todayY, todayM, todayD)
            }

            override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                showDateText.text = buildString {
                    append(day.toString())
                    append(" / ")
                    append(month + 1)
                    append(" / ")
                    append(year)
                }
                dayBirth = day
                monthBirth = month
                yearBirth = year
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
        //error messages that can be displayed in case of wrong input on click on validate
        val emptyFirstname: String = "* You forgot to indicate your firstname"
        val incorrectFirstname: String = "* Your firstname must be composed only of letters"
        val emptySurname: String = "* You forgot to indicate your surname"
        val incorrectSurname: String = "* Your surname must be composed only of letters"
        val emptyDate: String = "* You forgot to indicate your birth date"
        val incorrectDate: String = "* Your birthdate is impossible at this date"

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
            var allDataCorrect: Boolean = true

            //check the firstname
            val firstname = inputFirstnameText.text.toString()
            if (firstname == "") {
                allDataCorrect = false
                errorFirstnameText.text = emptyFirstname
                errorFirstnameText.setTextColor(Color.RED)
            } else if (!checkName(firstname)) {
                allDataCorrect = false
                errorFirstnameText.text = incorrectFirstname
                errorFirstnameText.setTextColor(Color.RED)
            } else {
                errorFirstnameText.text = ""
            }

            //check the surname
            val surname = inputSurnameText.text.toString()
            if (surname == "") {
                allDataCorrect = false
                errorSurnameText.text = emptySurname
                errorSurnameText.setTextColor(Color.RED)
            } else if (!checkName(surname)) {
                allDataCorrect = false
                errorSurnameText.text = incorrectSurname
                errorSurnameText.setTextColor(Color.RED)
            } else {
                errorSurnameText.text = ""
            }

            //check the date
            val dateStr: String = showDateText.text.toString()
            val dateOfBirth: Date = Date(yearBirth, monthBirth, dayBirth)
            val minTodayAge: Date = Date(todayY - MIN_AGE, todayM, todayD)
            val maxTodayAge: Date = Date(todayY - MAX_AGE, todayM, todayD)
            if (dateOfBirth < maxTodayAge || dateOfBirth > minTodayAge) {
                allDataCorrect = false
                errorDateText.text = incorrectDate
                errorDateText.setTextColor(Color.RED)
            } else if (dateStr == "dd/mm/yyyy") {
                allDataCorrect = false
                errorDateText.text = emptyDate
                errorDateText.setTextColor(Color.RED)
            } else {
                errorDateText.text = ""
            }
            if (allDataCorrect) {
                database.setPersonalInfo(userName, firstname, surname, dateOfBirth)
            }
        }
        return view
    }
}

/**
 * Helper function to check if a name is in the correct format
 * @param name to be checked
 * @return true if the name is in the correct format, false otherwise
 */
private fun checkName(name: String): Boolean {
    val tabChar: CharArray = name.toCharArray()
    for (i in 0..(name.length - 1)) {
        if (!(tabChar[i].isLetter() || tabChar[i] == '-')) {
            return false
        }
    }
    return true
}
