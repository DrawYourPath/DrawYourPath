package com.epfl.drawyourpath.database


import com.epfl.drawyourpath.userProfile.UserModel
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class Database {
    //name of the different attributes inside the user account
    val usernameFile: String = "username"
    val firstnameFile: String = "firstname"
    val surnameFile: String = "surname"
    val emailFile: String = "email"
    val dateOfBirthFile: String = "dateOfBirth"
    val distanceGoalFile: String = "distanceGoal"
    val activityTimeGoalFile: String = "activityTimeGoal"
    val nbOfPathsGoalFile: String = "nbOfPathsGoal"

    /**
     * This function is used to know if a certain user is already store in the database
     * @param userId that correspond to the user
     * @return a future that indicate if the user is store on the database
     */
    abstract fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean>

    /**
     * This function will return a future that give the username in function of the userId
     * @param userId of the user
     * @return a future that give the username of the user
     */
    abstract fun getUsernameFromUserId(userId: String): CompletableFuture<String>

    /**
     * This function will return a future that give the userId in function of the username
     * @param username of the user
     * @return a future that give the userId of the user
     */
    abstract  fun getUserIdFromUsername(username: String): CompletableFuture<String>

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    abstract fun isUsernameAvailable(userName: String): CompletableFuture<Boolean>

    /**
     * This function will update the username of the user if the username proposed is available(not taken by another user)
     * @param username proposed by the user(will be set to the userprofile if it's available)
     * @return the future that indicate if the username have been updated
     */
    abstract fun updateUsername(username: String): CompletableFuture<Boolean>

    /**
     * This function will add the username to the database as link to the userId of the user authenticate on the app
     * and create a user account on the database for the user authenticate with is userId and his username as parameter
     * of his user account
     * @param username username that the user want to set in the database
     * @return a future that indicate if the user account has been successfully created
     */
    abstract fun setUsername(username: String): CompletableFuture<Boolean>

    /**
     * This function is used to initialize the user profile information with UserModel give in parameter
     * @param userModel used for the initialization of the user profile
     * @return a future that indicate if the user profile has been correctly initiate
     */
    abstract fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean>

    /**
     * This function is used to get the user account of the user with userId
     * @param userId of the user that we want to retrieve is account
     * @return a future that return the UserModel corresponding to this user account
     */
    abstract  fun getUserAccount(userId: String): CompletableFuture<UserModel>

    /**
     * This function will return the UserModel of the user logged on the app
     * @return a future that give the userModel of the user logged on the app
     */
    abstract fun getLoggedUserAccount(): CompletableFuture<UserModel>

    /**
     * This function will set the daily distance goal to the database(in kilometer) of the user logged
     * @param distanceGoal new distance goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract  fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean>

    /**
     * This function will set the daily activity time goal to the database(in minutes) of the user logged
     * @param activityTimeGoal new activity time goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract  fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean>

    /**
     * This function will set the daily number of paths goal to the database(integer) of the user logged
     * @param nbOfPathsGoal new number of paths goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract  fun setNbOfPathsGoal(nbOfPathsGoal: Int):CompletableFuture<Boolean>
}