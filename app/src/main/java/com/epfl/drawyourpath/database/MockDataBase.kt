package com.epfl.drawyourpath.database

import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()
    var usernameToPersonalInfoDataBase: HashMap<String, PersonalInfo> = HashMap()
    var usernameToUserGoalsDataBase: HashMap<String, UserGoals> = HashMap()

    override fun isUserNameAvailable(userName: String): CompletableFuture<Boolean> {
        //add an element in the user database to test the UI
        usersDataBase.add("albert")

        return CompletableFuture.completedFuture(!usersDataBase.contains(userName))
    }

    override fun setUserName(userName: String) {
        usersDataBase.add(userName)
    }

    override fun setPersonalInfo(
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate
    ) {
        val userPersonalInfo: PersonalInfo = PersonalInfo(firstname, surname, dateOfBirth)
        usernameToPersonalInfoDataBase.put(username, userPersonalInfo)
    }

    override fun setUserGoals(
        username: String,
        distanceGoal: Int,
        timeGoal: Int,
        nbOfPathsGoal: Int
    ) {
        val userGoals: UserGoals = UserGoals(username, distanceGoal, timeGoal, nbOfPathsGoal)
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