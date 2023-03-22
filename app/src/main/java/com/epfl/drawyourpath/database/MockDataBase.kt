package com.epfl.drawyourpath.database

import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()
    var usernameToPersonalInfoDataBase: HashMap<String, PersonalInfo> = HashMap()
    var usernameToUserGoalsDataBase: HashMap<String, UserGoals> = HashMap()

    //clean database
    //link to username to userId
    var usernameToUserId: HashMap<String, String> = HashMap()


    var userIdToUsername: HashMap<String, String> = HashMap() //1=userId 2=username
    var userIdToDistanceGoal: HashMap<String, Double> = HashMap() //1:userid, 2:distance goal
    var userIdToActivityTimeGoal: HashMap<String, Double> =
        HashMap() //1:userid, 2:activity time goal
    var userIdToNbOfPathsGoal: HashMap<String, Int> = HashMap() //1:userid, 2:number of paths goal

    val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    val usernameTest: String = "albert"
    val distanceGoalTest: Double = 10.0
    val timeGoalTest: Double = 60.0
    val nbOfPathsGoalTest: Int = 5

    init {
        //add elements to the list of the user with userId for the tests
        usersDataBase.add("albert")

        //clean database
        usernameToUserId.put(usernameTest, userIdTest)
        usernameToUserId.put("hugo", "exId")
        userIdToUsername.put(userIdTest, usernameTest)
        userIdToDistanceGoal.put(userIdTest, distanceGoalTest)
        userIdToActivityTimeGoal.put(userIdTest, timeGoalTest)
        userIdToNbOfPathsGoal.put(userIdTest, nbOfPathsGoalTest)
    }

    override fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(userIdToUsername.contains(userId))
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(userIdToUsername.get(userId))
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(!usersDataBase.contains(userName))
    }

    override fun updateUsername(username: String, userId: String): CompletableFuture<Boolean> {
        val available = !usernameToUserId.contains(username)
        if (available) {
            //remove the past username has taken
            val pastUsername = userIdToUsername.get(userId)
            usernameToUserId.remove(pastUsername)
            //add the new username linck to the userId
            usernameToUserId.put(username, userId)
            //edit the userProfile
            userIdToUsername.put(userId, username)
        }
        return CompletableFuture.completedFuture(available)
    }

    override fun setUsername(username: String) {
        usersDataBase.add(username)
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
        distanceGoal: Double,
        timeGoal: Double,
        nbOfPathsGoal: Int
    ) {
        val userGoals: UserGoals = UserGoals(username, distanceGoal, timeGoal, nbOfPathsGoal)
        usernameToUserGoalsDataBase.put(username, userGoals)
    }

    override fun setDistanceGoal(userId: String, distanceGoal: Double): CompletableFuture<Boolean> {
        userIdToDistanceGoal.put(userId, distanceGoal)
        return CompletableFuture.completedFuture(userIdToDistanceGoal.get(userId) == distanceGoal)
    }

    override fun setActivityTimeGoal(
        userId: String,
        activityTimeGoal: Double
    ): CompletableFuture<Boolean> {
        userIdToActivityTimeGoal.put(userId, activityTimeGoal)
        return CompletableFuture.completedFuture(userIdToActivityTimeGoal.get(userId) == activityTimeGoal)
    }

    override fun setNbOfPathsGoal(userId: String, nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        userIdToNbOfPathsGoal.put(userId, nbOfPathsGoal)
        return CompletableFuture.completedFuture(userIdToNbOfPathsGoal.get(userId) == nbOfPathsGoal)
    }
}

//create some enum to facilitate the tests
class PersonalInfo(firstname: String, surname: String, dateOfBirth: LocalDate) {
    private var firstname = firstname
    private var surname = surname
    private var dateOfBirth = dateOfBirth
}

class UserGoals(username: String, distanceGoal: Double, timeGoal: Double, nbOfPathsGoal: Int) {
    private var distanceGoal = distanceGoal
    private var timeGoal = timeGoal
    private var nbOfPathsGoal = nbOfPathsGoal
}