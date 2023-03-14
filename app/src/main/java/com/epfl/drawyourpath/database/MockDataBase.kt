package com.epfl.drawyourpath.database

import android.graphics.Color
import android.widget.TextView
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()
    var usernameToPersonalInfoDataBase: HashMap<String, PersonalInfo> = HashMap()
    var usernameToUserGoalsDataBase: HashMap<String, UserGoals> = HashMap()

    override fun isUserNameAvailable(userName: String): CompletableFuture<Boolean> {
        //add an element in the user database to test the UI
        usersDataBase.add("albert")

        val future = CompletableFuture<Boolean>()
        if(usersDataBase.contains(userName)){
            future.complete(false)
        }else{
            future.complete(true)
        }
        return future
    }

    override fun setUserName(userName: String) {
        usersDataBase.add(userName)
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