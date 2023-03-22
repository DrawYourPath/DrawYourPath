package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.userProfile.UserModel
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    //clean database
    //link to username to userId
    var usernameToUserId: HashMap<String, String> = HashMap()

    //user account
    val userIdToUserAccount: HashMap<String, UserModel> = HashMap()
    var userIdToUsername: HashMap<String, String> = HashMap() //1=userId 2=username

    val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    val userAuthTest: User = MockAuth.MOCK_USER
    val usernameTest: String = "albert"
    val distanceGoalTest: Double = 10.0
    val activityTimeGoalTest: Double = 60.0
    val nbOfPathsGoalTest: Int = 5
    val firstnameTest = "Hugo"
    val surnameTest = "Hof"
    val takenUsername = "nathan"
    val dateOfBirthTest = LocalDate.of(2000, 2, 20)
    val userModelTest: UserModel

    init {
        usernameToUserId.put(usernameTest, userIdTest)
        usernameToUserId.put(takenUsername, "exId")

        userIdToUsername.put(userIdTest, usernameTest)
        userModelTest = UserModel(
            userAuthTest,
            firstnameTest,
            surnameTest,
            dateOfBirthTest,
            distanceGoalTest,
            activityTimeGoalTest,
            nbOfPathsGoalTest,
            this
        )
        userIdToUserAccount.put(userIdTest, userModelTest)
    }

    override fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(userIdToUsername.contains(userId))
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(userIdToUsername.get(userId))
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(usernameToUserId.get(username))
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(!usernameToUserId.contains(userName))
    }

    override fun updateUsername(username: String): CompletableFuture<Boolean> {
        return isUsernameAvailable(username).thenApply {
            if (it) {
                //remove the past username has taken
                val pastUsername = userIdToUsername.get(userIdTest)
                usernameToUserId.remove(pastUsername)
                //add the new username link to the userId
                usernameToUserId.put(username, userIdTest)
                //edit the userProfile
                userIdToUsername.put(userIdTest, username)
            }
            it
        }
    }

    override fun setUsername(username: String): CompletableFuture<Boolean> {
        return isUsernameAvailable(username).thenApply {
            if (it) {
                usernameToUserId.put(username, userIdTest)
                userIdToUsername.put(userIdTest, username)
            }
            it
        }
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean> {
        userIdToUserAccount.put(userIdTest, userModel)
        return CompletableFuture.completedFuture(true)
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        return CompletableFuture.completedFuture(userIdToUserAccount.get(userId))
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        return getUserAccount(userIdTest)
    }

    override fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        if(distanceGoal <= 0.0){
            throw java.lang.Error("The distance goal can't be less or equal than 0.")
        }
        val updatedUser = UserModel(userAuthTest, firstnameTest, surnameTest, dateOfBirthTest, distanceGoal,
            activityTimeGoalTest, nbOfPathsGoalTest, this)
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        if(activityTimeGoal <= 0.0){
            throw java.lang.Error("The activity time goal can't be less or equal than 0.")
        }
        val updatedUser = UserModel(userAuthTest, firstnameTest, surnameTest, dateOfBirthTest, distanceGoalTest,
            activityTimeGoal, nbOfPathsGoalTest, this)
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun setNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        if(nbOfPathsGoal <= 0.0){
            throw java.lang.Error("The number of paths goal can't be less or equal than 0.")
        }
        val updatedUser = UserModel(userAuthTest, firstnameTest, surnameTest, dateOfBirthTest, distanceGoalTest,
            activityTimeGoalTest, nbOfPathsGoal, this)
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }
}