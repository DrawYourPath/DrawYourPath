package com.epfl.drawyourpath.database

import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()
    var usernameToPersonalInfoDataBase: HashMap<String, PersonalInfo> = HashMap()
    var usernameToUserGoalsDataBase: HashMap<String, UserGoals> = HashMap()

    //clean database
    var userNameToUserId: HashMap<String, String> = HashMap()
    var userProfileWithUserNameLinckWithId: HashMap<String, String> = HashMap() //1=userId 2=username


    override fun isUserStoreOnDatabase(userId: String): CompletableFuture<Boolean> {
        //add an element to the list of the user with userId for the tests
        userProfileWithUserNameLinckWithId.put("id1234", "albert")

        return CompletableFuture.completedFuture(userProfileWithUserNameLinckWithId.contains(userId))
    }

    override fun isUserNameAvailable(userName: String): CompletableFuture<Boolean> {
        //add an element in the user database to test the UI
        usersDataBase.add("albert")

        return CompletableFuture.completedFuture(!usersDataBase.contains(userName))
    }

    override fun updateUsername(username: String, userId: String): CompletableFuture<Boolean> {
        //for the tests
        userNameToUserId.put("albert", "id1234")
        userNameToUserId.put("hugo", "id55514")
        userProfileWithUserNameLinckWithId.put("id1234", "albert")

        val available = !userNameToUserId.contains(username)
        if(available){
            //remove the past username has taken
            val pastUsername = userProfileWithUserNameLinckWithId.get(userId)
            userNameToUserId.remove(pastUsername)
            //add the new username linck to the userId
            userNameToUserId.put(username, userId)
            //edit the userProfile
            userProfileWithUserNameLinckWithId.put(userId, username)
        }
        return CompletableFuture.completedFuture(available)
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