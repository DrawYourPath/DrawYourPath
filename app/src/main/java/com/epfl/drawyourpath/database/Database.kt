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
    abstract fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean>

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    abstract fun isUsernameAvailable(userName: String): CompletableFuture<Boolean>

    /**
     * This function will update the username of the user if the username proposed is available(not taken by another user)
     * @param username prposed by the user(will be set to the userprofile if it's available)
     * @param userId userId of the user
     * @return the future that indicate if the username have been updated
     */
    abstract fun updateUsername(username: String,userId: String): CompletableFuture<Boolean>

    /**
     * This function will add the user Name to the database to create a new user profile
     * @param username username that the user want to set in the database
     */
    abstract fun setUsername(username: String)

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
        distanceGoal: Double,
        timeGoal: Double,
        nbOfPathsGoal: Int
    )

    /**
     * This function will set the daily distance goal to the database(in kilometer)
     * @param userId that correspond to the user
     * @param distanceGoal new distance goal of the user
     */
    abstract  fun setDistanceGoal(userId: String, distanceGoal: Double)

    /**
     * This function will set the daily activity time goal to the database(in minutes)
     * @param userId that correspond to the user
     * @param activityTimeGoal new activity time goal of the user
     */
    abstract  fun setActivityTimeGoalGoal(userId: String, activityTimeGoal: Double)

    /**
     * This function will set the daily number of paths goal to the database(integer)
     * @param userId that correspond to the user
     * @param nbOfPathsGoal new number of paths goal of the user
     */
    abstract  fun setNbOfPathsGoalGoal(userId: String, nbOfPathsGoal: Double)
}