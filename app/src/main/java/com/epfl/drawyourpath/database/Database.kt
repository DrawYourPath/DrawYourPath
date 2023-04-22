package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.concurrent.CompletableFuture

abstract class Database {
    companion object {
        // name of the different attributes inside the user account
        const val usernameFile: String = "username"
        const val firstnameFile: String = "firstname"
        const val surnameFile: String = "surname"
        const val emailFile: String = "email"
        const val dateOfBirthFile: String = "dateOfBirth"
        const val profilePhotoFile: String = "profilePhoto"
        const val friendsListFile: String = "friendsList"

        // current daily goal
        const val currentDistanceGoalFile: String = "distanceGoal"
        const val currentActivityTimeGoalFile: String = "activityTimeGoal"
        const val currentNOfPathsGoalFile: String = "nbOfPathsGoal"

        // daily user goals
        const val dailyGoalsFile: String = "dailyGoals"
        const val expectedDistanceFile: String = "expectedDistance"
        const val expectedActivityTimeFile: String = "expectedActivityTime"
        const val expectedNbOfPathsFile: String = "expectedNbOfPaths"
        const val obtainedDistanceFile: String = "obtainedDistance"
        const val obtainedActivityTimeFile: String = "obtainedActivityTime"
        const val obtainedNbOfPathsFile: String = "obtainedNbOfPaths"

        // user achievements, will be used later for the trophies
        const val achievementsFile: String = "achievements"
        const val totalDistanceFile: String = "totalDistance"
        const val totalActivityTimeFile: String = "totalActivityTime"
        const val totalNbOfPathsFile: String = "totalNbOfPaths"

        // run history
        const val runsHistoryFile: String = "runsHistory"
    }

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
    abstract fun getUserIdFromUsername(username: String): CompletableFuture<String>

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
    abstract fun updateUsername(username: String): CompletableFuture<Unit>

    /**
     * This function will add the username to the database as link to the userId of the user authenticate on the app
     * and create a user account on the database for the user authenticate with is userId and his username as parameter
     * of his user account
     * @param username username that the user want to set in the database
     * @return a future that indicate if the user account has been successfully created
     */
    abstract fun setUsername(username: String): CompletableFuture<Unit>

    /**
     * This function is used to initialize the user profile information with UserModel give in parameter
     * @param userModel used for the initialization of the user profile
     * @return a future that indicate if the user profile has been correctly initiate
     */
    abstract fun initUserProfile(userModel: UserModel): CompletableFuture<Unit>

    /**
     * This function is used to get the user account of the user with userId
     * @param userId of the user that we want to retrieve is account
     * @return a future that return the UserModel corresponding to this user account
     */
    abstract fun getUserAccount(userId: String): CompletableFuture<UserModel>

    /**
     * This function will return the UserModel of the user logged on the app
     * @return a future that give the userModel of the user logged on the app
     */
    abstract fun getLoggedUserAccount(): CompletableFuture<UserModel>

    /**
     * This function will set the current daily distance goal to the database(in kilometer) of the user logged
     * @param distanceGoal new distance goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract fun setCurrentDistanceGoal(distanceGoal: Double): CompletableFuture<Unit>

    /**
     * This function will set the current daily activity time goal to the database(in minutes) of the user logged
     * @param activityTimeGoal new activity time goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract fun setCurrentActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit>

    /**
     * This function will set the current daily number of paths goal to the database(integer) of the user logged
     * @param nbOfPathsGoal new number of paths goal of the user
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract fun setCurrentNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit>

    /**
     * This function will set the profilePhoto to the database (Bitmap) of the user logged
     * @param photo that will be set
     * @return a future that indicate if the photo has been correctly set to the database
     */
    abstract fun setProfilePhoto(photo: Bitmap): CompletableFuture<Unit>

    /**
     * This function will add a user to the the friends list of the current user with his userId if this user is present on the database
     * @param userId of the user that we want to add to the friendsList of the current user
     * @throws an Error if the user that we want to added to the friends list is not present on the database.
     * @return a future that indicate if the user has been correctly added to the current user friends list
     */
    abstract fun addUserToFriendsList(userId: String): CompletableFuture<Unit>

    /**
     * This function will remove a user to the the friends list of the current user with his userId
     * @param userId of the user that we want to remove to the friendsList of the current user
     * @throws an Error if the user that we want to removed is not present on the database.
     * @return a future that indicate if the user has been correctly removed to the current user friends list
     */
    abstract fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit>

    /**
     * This function will add a run to the history of runs, using its starting time as a key
     * @param run to be stored
     * @return a future that indicate if the run has been correctly added to the history in the database
     */
    abstract fun addRunToHistory(run: Run): CompletableFuture<Unit>

    /**
     * This function will remove a run from the history of runs, using its starting time as a key
     * @param run to be removed
     * @return a future that indicate if the run has been correctly removed from the history in the database
     */
    abstract fun removeRunFromHistory(run: Run): CompletableFuture<Unit>

    /**
     * This function is used to add a dailyGoal in the database to the list of dailyGoals realized by the user logged
     * in the dailyGoals section(the dailyGoal will be update if dailyGoal at this date already exist in the database).
     * @param dailyGaol that we want to add in the database
     * @return a future that indicate if the daily Goal have been correctly added to the database
     */
    abstract fun addDailyGoal(dailyGoal: DailyGoal): CompletableFuture<Unit>

    /**
     * Function used to update on the database the user achievements(total distance, total activity time and total nb of paths draw by the user)
     * with the result at the end of a drawing activity(remark: the total number of path will be incremented by one, since only one draw
     * can be achieved each drawing activity).
     * @param distanceDrawing distance run by user to achieve the drawing
     * @param activityTimeDrawing time take by the user to realized the drawing
     * @return a future that indicate if the achievements of the user have been correctly updated.
     */
    abstract fun updateUserAchievements(distanceDrawing: Double, activityTimeDrawing: Double): CompletableFuture<Unit>
}
