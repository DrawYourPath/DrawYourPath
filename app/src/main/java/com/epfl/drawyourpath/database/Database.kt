package com.epfl.drawyourpath.database


import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class Database {
    /**
     * This function is used to know if a certain user is already store in the database
     * @param userId that correspond to the user
     * @return a future that indicate if the user is store on the database
     */
    abstract fun isUserStoreOnDatabase(userId: String): CompletableFuture<Boolean>

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    abstract fun isUserNameAvailable(userName: String): CompletableFuture<Boolean>

    /**
     * This function will update the username of the user if the username proposed is available(not taken by another user)
     * @param username prposed by the user(will be set to the userprofile if it's available)
     * @param userId userId of the user
     * @return the future that indicate if the username have been updated
     */
    abstract fun updateUsername(username: String,userId: String): CompletableFuture<Boolean>

    /**
     * This function will add the user Name to the database to create a new user profile
     * @param userName userName that the user want to set in the database
     */
    abstract fun setUserName(userName: String)

    /**
     * This function will set the personal info of a user to the database (firstname, surname, date of birth).
     * @param username username associated to the users(where the data will be affected)
     * @param firstname firstname of the user
     * @param surname surname of the user
     * @param dateOfBirth date of birth of the user
     */
    abstract fun setPersonalInfo(
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate
    )

    /**
     * This function will set the goals in a day of a user to the database(distance per day, minute of exercises per day, number of path to draw per day).
     * @param username username associated to the users(where the data will be affected)
     * @param distanceGoal distance goal in kilometer
     * @param timeGoal activity time goal in minutes
     * @param nbOfPaths number of paths goal
     */
    abstract fun setUserGoals(
        username: String,
        distanceGoal: Int,
        timeGoal: Int,
        nbOfPathsGoal: Int
    )
}