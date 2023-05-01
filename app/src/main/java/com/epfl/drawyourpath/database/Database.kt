package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.concurrent.CompletableFuture

data class UserGoals(
    val paths: Long? = null,
    val distance: Double? = null,
    val activityTime: Long? = null,
)

data class UserData(
    val username: String? = null,
    val userId: String? = null,
    val firstname: String? = null,
    val surname: String? = null,
    val birthDate: Long? = null,
    val email: String? = null,
    val goals: UserGoals? = null,
    val picture: String? = null,
    val friendList: List<String>? = null,
    val runs: List<Run>? = null,
    val dailyGoals: List<DailyGoal>? = null,
)

abstract class Database {
    /**
     * This function is used to know if a certain user is already stored in the database
     * @param userId that corresponds to the user
     * @return a future that indicates if the user is stored on the database
     */
    abstract fun isUserInDatabase(userId: String): CompletableFuture<Boolean>

    /**
     * This function is used to know if a certain user is already stored in the database
     * @param tournamentId that corresponds to the tournament
     * @return a future that indicates if the tournament is stored on the database
     */
    abstract fun isTournamentInDatabase(tournamentId: String): CompletableFuture<Boolean>

    /**
     * This function will return a future that give the username in function of the userId
     * @param userId of the user
     * @return a future that gives the username of the user
     */
    abstract fun getUsername(userId: String): CompletableFuture<String>

    /**
     * This function will return a future that give the userId in function of the username
     * @param username of the user
     * @return a future that gives the userId of the user
     */
    abstract fun getUserIdFromUsername(username: String): CompletableFuture<String>

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicates if the username is available
     */
    abstract fun isUsernameAvailable(userName: String): CompletableFuture<Boolean>

    /**
     * This function will add the username to the database as link to the userId of the user authenticate on the app
     * and create a user account on the database for the user authenticate with is userId and his username as parameter
     * of his user account
     * @param username username that the user want to set in the database
     * @return a future that indicates if the user account has been successfully created
     */
    abstract fun setUsername(userId: String, username: String): CompletableFuture<Unit>

    /**
     * Creates a new user in the database.
     */
    abstract fun createUser(userId: String, userData: UserData): CompletableFuture<Unit>

    /**
     * This function is used to initialize the user profile information with UserModel give in parameter
     * @param userData used for the initialization of the user profile
     * @return a future that indicates if the user profile has been correctly initiate
     */
    abstract fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit>

    /**
     * This function is used to get the user account of the user with userId
     * @param userId of the user that we want to retrieve is account
     * @return a future that returns the UserModel corresponding to this user account
     */
    abstract fun getUserData(userId: String): CompletableFuture<UserData>

    /**
     * Set the current daily distance goal to the database(in kilometer) of the user logged
     * @param distanceGoal new distance goal of the user
     * @return a future that indicates if the goal has been correctly set to the database
     */
    abstract fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit>

    /**
     * This function will set the profilePhoto to the database (Bitmap) of the user logged
     * @param photo that will be set
     * @return a future that indicates if the photo has been correctly set to the database
     */
    abstract fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit>

    /**
     * This function will add a user to the the friends list of the current user with his userId if this user is present on the database
     * @param userId of the user that we want to add to the friendsList of the current user
     * @throws an Error if the user that we want to added to the friends list is not present on the database.
     * @return a future that indicates if the user has been correctly added to the current user friends list
     */
    abstract fun addFriend(userId: String, targetFriend: String): CompletableFuture<Unit>

    /**
     * This function will remove a user to the the friends list of the current user with his userId
     * @param userId of the user that we want to remove to the friendsList of the current user
     * @throws an Error if the user that we want to removed is not present on the database.
     * @return a future that indicates if the user has been correctly removed to the current user friends list
     */
    abstract fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit>

    /**
     * This function will add a run to the history of runs, using its starting time as a key
     * @param run to be stored
     * @return a future that indicates if the run has been correctly added to the history in the database
     */
    abstract fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit>

    /**
     * This function will remove a run from the history of runs, using its starting time as a key
     * @param run to be removed
     * @return a future that indicates if the run has been correctly removed from the history in the database
     */
    abstract fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit>

    /**
     * This function is used to add a dailyGoal in the database to the list of dailyGoals realized by the user logged
     * in the dailyGoals section(the dailyGoal will be update if dailyGoal at this date already exist in the database).
     * @param dailyGaol that we want to add in the database
     * @return a future that indicates if the daily Goal have been correctly added to the database
     */
    abstract fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit>

    /**
     * Function used to update on the database the user achievements(total distance, total activity time and total nb of paths draw by the user)
     * with the result at the end of a drawing activity(remark: the total number of path will be incremented by one, since only one draw
     * can be achieved each drawing activity).
     * @param distanceDrawing distance run by user to achieve the drawing
     * @param activityTimeDrawing time take by the user to realized the drawing
     * @return a future that indicates if the achievements of the user have been correctly updated.
     */
    abstract fun updateUserAchievements(
        userId: String,
        distanceDrawing: Double,
        activityTimeDrawing: Double
    ): CompletableFuture<Unit>

    /**
     * Function used to add a tournament to the database.
     * @param tournament the tournament to store.
     * @return a future that indicates if the tournament has been stored correctly.
     */
    abstract fun addTournament(tournament: Tournament): CompletableFuture<Unit>

    /**
     * Function used to add a participant to a tournament.
     * @param userId the id of the user to register.
     * @param tournamentId the id of the tournament in which we register the user.
     * @return a future that indicates if the user has been registered correctly.
     */
    abstract fun addUserToTournament(
        userId: String,
        tournamentId: String
    ): CompletableFuture<Unit>

    /**
     * Function used to remove/delete a tournament based on its id. This should only be used
     * by the creator of the tournament, the check should be done before calling this function.
     * @param tournamentId the id of the tournament to remove.
     * @return a future that indicated if the tournament has been correctly removed.
     */
    abstract fun removeTournament(tournamentId: String): CompletableFuture<Unit>

    /**
     * Function used to unregister a participant from a tournament.
     * @param userId the id of the user we want to unregister.
     * @param tournamentId the id of the tournament from which we want to remove the user.
     * @return a future that indicates if the user has been correctly unregistered.
     */
    abstract fun removeUserFromTournament(
        userId: String,
        tournamentId: String
    ): CompletableFuture<Unit>
}
