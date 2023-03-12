package com.github.drawyourpath.bootcamp.database

import android.graphics.Color
import android.widget.TextView
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()
    var usernameToPersonalInfoDataBase: HashMap<String, PersonalInfo> = HashMap()
    var usernameToUserGoalsDataBase: HashMap<String, UserGoals> = HashMap()

    override fun isUserNameAvailable(userName: String, outputText: TextView): Boolean {
        //add an element in the user database to test the UI
        System.out.println("je suis en ligne")
        usersDataBase.add("albert")
        val unAvailableOutput: String = buildString {
            append("*The username ")
            append(userName)
            append(" is NOT available !")
        }
        val availableOutput: String = buildString {
            append("*The username ")
            append(userName)
            append(" is available !")
        }
        if (userName == "") {
            outputText.text = buildString { append("The username can't be empty !") }
            outputText.setTextColor(Color.RED)
            return false
        } else if (usersDataBase.contains(userName)) {
            outputText.text = unAvailableOutput
            outputText.setTextColor(Color.RED)
            return false
        } else {
            outputText.text = availableOutput
            outputText.setTextColor(Color.GREEN)
            return true
        }
    }

    override fun setUserName(userName: String, outputText: TextView): Boolean {
        if (isUserNameAvailable(userName, outputText)) {
            usersDataBase.add(userName)
            return true
        }
        return false
    }

    override fun setPersonalInfo(username: String, firstname: String, surname: String, dateOfBirth: LocalDate) {
        val userPersonalInfo: PersonalInfo = PersonalInfo(firstname, surname, dateOfBirth)
        usernameToPersonalInfoDataBase.put(username, userPersonalInfo)
    }

    override fun setUserGoals(username: String, distanceGoal: Int, timeGoal: Int, nbOfPathsGoal: Int) {
        val userGoals: UserGoals = UserGoals(username,distanceGoal,timeGoal, nbOfPathsGoal)
        usernameToUserGoalsDataBase.put(username, userGoals)
    }
}

//create some enum to facilitate the tests
class PersonalInfo(firstname: String, surname: String, dateOfBirth: LocalDate) {
    private var firstname = firstname
    private var surname = surname
    private var dateOfBirth = dateOfBirth
}

class UserGoals(username: String, distanceGoal: Int, timeGoal: Int, nbOfPathsGoal: Int) {
    private var distanceGoal = distanceGoal
    private var timeGoal = timeGoal
    private var nbOfPathsGoal = nbOfPathsGoal
}