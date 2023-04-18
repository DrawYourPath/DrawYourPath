package com.epfl.drawyourpath.userProfile

import android.graphics.Bitmap
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.path.Run
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture

class UserModel {
    //the userId of the user
    private val userId: String

    //the userName is chosen by the user and can be modify
    private var username: String

    //the email is given at the beginning by the authentication part and can be modify
    private var emailAddress: String

    //the firstname can't be modify after initialization
    private val firstname: String

    //the surname can't be modify after initialization
    private val surname: String

    //the date of birth can't be modify after initialization
    private val dateOfBirth: LocalDate

    //the distance goal is initialize at the profile creation and can be modify
    private var distanceGoal: Double

    //the activity time goal is initialize at the profile creation and can be modify
    private var activityTimeGoal: Double

    //the number of path goal is initialize at the profile creation and can be modify
    private var nbOfPathsGoal: Int

    //database where the user is store online
    private var database: Database

    //friend list
    private var friendsList: List<String> //a list of userId

    //profile photo, can be null if the user don't want to
    private var profilePhoto: Bitmap? = null

    //runs history
    private var runsHistory: List<Run>


    /**
     * This constructor will create a new user based on the user model of the app (constructor at the the profile creation)
     * @param userAuth user authenticate give by the login
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @throws error if the inputs are incorrect
     */
    constructor(
        userAuth: User,
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate,
        distanceGoal: Double,
        activityTimeGoal: Double,
        nbOfPathsGoal: Int,
        database: Database
    ) {
        this.database = database

        //obtain the userId and the email give by the authentication
        this.userId = userAuth.getUid()
        this.emailAddress = userAuth.getEmail()

        //obtain the username
        this.username = username

        //check the format of the firstname
        checkNameFormat(firstname, "firstname")
        this.firstname = firstname

        //check the format of the surname
        checkNameFormat(surname, "surname")
        this.surname = surname

        //check that the birth date respect the age condition of the app(10<=age<=100)
        checkDateOfBirth(dateOfBirth)
        this.dateOfBirth = dateOfBirth

        //test the goals, the goals can't be equal or less than 0
        checkDistanceGoal(distanceGoal)
        this.distanceGoal = distanceGoal

        checkActivityTimeGoal(activityTimeGoal)
        this.activityTimeGoal = activityTimeGoal

        checkNbOfPathsGoal(nbOfPathsGoal)
        this.nbOfPathsGoal = nbOfPathsGoal

        this.friendsList = ArrayList()
        this.profilePhoto = null

        this.runsHistory = ArrayList()
    }

    /**
     * This constructor will create a new user based on the user model of the app
     * @param userId of the user
     * @param emailAddress of the user
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @param friendsList the friendsList of the user(with a default empty friendsList
     * @throws error if the inputs are incorrect
     */
    constructor(
        userId: String,
        emailAddress: String,
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate,
        distanceGoal: Double,
        activityTimeGoal: Double,
        nbOfPathsGoal: Int,
        profilePhoto: Bitmap?,
        friendsList: List<String>,
        runsHistory: List<Run>,
        database: Database
    ) {
        this.database = database

        //obtain the userId and the email give by the authentication
        this.userId = userId
        this.emailAddress = emailAddress

        //obtain the username
        this.username = username

        //check the format of the firstname
        checkNameFormat(firstname, "firstname")
        this.firstname = firstname

        //check the format of the surname
        checkNameFormat(surname, "surname")
        this.surname = surname

        //check that the birth date respect the age condition of the app(10<=age<=100)
        checkDateOfBirth(dateOfBirth)
        this.dateOfBirth = dateOfBirth

        //test the goals, the goals can't be equal or less than 0
        checkDistanceGoal(distanceGoal)
        this.distanceGoal = distanceGoal

        checkActivityTimeGoal(activityTimeGoal)
        this.activityTimeGoal = activityTimeGoal

        checkNbOfPathsGoal(nbOfPathsGoal)
        this.nbOfPathsGoal = nbOfPathsGoal

        this.friendsList = friendsList
        this.profilePhoto = profilePhoto

        this.runsHistory = runsHistory
    }

    /**
     * Get the userId of the user
     * @return userId of the user
     */
    fun getUserId(): String {
        return userId
    }

    /**
     * Get the username of the user
     * @return username of the user
     */
    fun getUsername(): String {
        return username
    }

    /**
     * Use this function to modify the username(the username will be modify only if it is available on the database)
     * @param username that we want to set
     */
    fun setUsername(username: String): CompletableFuture<Boolean> {
        return database.updateUsername(username).thenApply {
            if (it) {
                this.username = username
            }
            it
        }
    }

    /**
     * Get the email address of the user
     * @return email address of the user
     */
    fun getEmailAddress(): String {
        return emailAddress
    }

    /*
    /**
     * Use this function to modify the email address of the user
     * @param email new email address
     */
    fun setEmailAddress(email: String){
        if(!checkEmail(email)){
            throw java.lang.Error("Invalid email format !")
        }
        this.emailAddress=email
    }
    */

    /**
     * Get the firstname of the user
     * @return firstname of the user
     */
    fun getFirstname(): String {
        return firstname
    }

    /**
     * Get the surname of the user
     * @return surname of the user
     */
    fun getSurname(): String {
        return surname
    }

    /**
     * Get the date of birth of the user
     * @return date of birth of the user
     */
    fun getDateOfBirth(): LocalDate {
        return dateOfBirth
    }

    /**
     * Get the daily distance goal of the user
     * @return daily distance goal of the user
     */
    fun getDistanceGoal(): Double {
        return distanceGoal
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        checkDistanceGoal(distanceGoal)
        return database.setDistanceGoal(distanceGoal).thenApply {
            if (it) {
                this.distanceGoal = distanceGoal
            }
            it
        }
    }

    /**
     * Get the daily activity time goal of the user
     * @return daily activity time goal of the user
     */
    fun getActivityTime(): Double {
        return activityTimeGoal
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param activityTimeGoal new daily activity time goal
     */
    fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        checkActivityTimeGoal(activityTimeGoal)
        return database.setActivityTimeGoal(activityTimeGoal).thenApply {
            if (it) {
                this.activityTimeGoal = activityTimeGoal
            }
            it
        }
    }

    /**
     * Get the daily number of paths goal of the user
     * @return daily number of paths goal of the user
     */
    fun getNumberOfPathsGoal(): Int {
        return nbOfPathsGoal
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPathsGoal new daily number of paths goal
     */
    fun setNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        checkNbOfPathsGoal(nbOfPathsGoal)
        return database.setNbOfPathsGoal(nbOfPathsGoal).thenApply {
            if (it) {
                this.nbOfPathsGoal = nbOfPathsGoal
            }
            it
        }
    }

    /**
     * Get the age of the user
     * @return the age of the user
     */
    fun getAge(): Int {
        return ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now()).toInt()
    }

    /**
     * This function will remove the user with userId to the friend list
     * @param userId of the user that we want to remove
     * @return a future that indicate if the user has been correctly removed from the friends list
     */
    fun removeFriend(userId: String): CompletableFuture<Unit> {
        if (!friendsList.contains(userId)) {
            throw Exception("This user with userId $userId is not in the friend list !")
        }
        return database.removeUserFromFriendlist(userId).thenApply {
            val interList = friendsList.toMutableList()
            interList.remove(userId)
            friendsList = interList
        }
    }

    /**
     * This function will add the user with userId to the friend list. To be added the user must be present in the database.
     * @param userId of the user that we want to add to the friend list
     * @return a future that indicate if the user was correctly added to the database
     */
    fun addFriend(userId: String): CompletableFuture<Unit> {
        return database.addUserToFriendsList(userId).thenApply {
            val interList = friendsList.toMutableList()
            interList.add(userId)
            friendsList = interList
        }
    }

    /**
     * This function will return the friend list of a user(a list the his friends userId's)
     * @return the friend list of the user
     */
    fun getFriendList(): List<String> {
        return this.friendsList
    }

    /**
     * This function will add a run to the history of the user in the local history and the database.
     * The key is the startTime, the history is sorted based on it.
     * @param run to be added to the history
     * @return a future that indicate if the run was correctly added to the database
     */
    fun addRunToHistory(run: Run): CompletableFuture<Unit> {
        return database.addRunToHistory(run).thenApply {
            val tmpList =
                runsHistory.filter { it.getStartTime() != run.getStartTime() }.toMutableList()
            tmpList.add(run)
            tmpList.sortBy { it.getStartTime() }
            runsHistory = tmpList
        }
    }

    /**
     * This function will remove a run from the history of the user in the local history and the database.
     * @param run to be removed from the history
     * @throws Exception if the path is not in the history
     * @return a future that indicate if the run was correctly removed from the database
     */
    fun removeRunFromHistory(run: Run): CompletableFuture<Unit> {
        if (!runsHistory.contains(run)) {
            val future = CompletableFuture<Unit>()
            future.completeExceptionally(Exception("This path is not in the history !"))
            return future
        }

        return database.removeRunFromHistory(run).thenApply {
            val tmpList = runsHistory.toMutableList()
            tmpList.remove(run)
            runsHistory = tmpList
        }
    }

    /**
     * This function will return the runs history of a user
     * @return the runs history of the user
     */
    fun getRunsHistory(): List<Run> {
        return runsHistory
    }

    /**
     * This function will  return the profile photo of the user or null if it doesn't exit
     * @return the profile photo if it exist
     */
    fun getProfilePhoto(): Bitmap? {
        return profilePhoto
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        return database.setProfilePhoto(photo).thenApply {
            if (it) {
                this.profilePhoto = photo
            }
            it
        }
    }

}

/**
 * Helper function to check if the name format of a given variableName is correct and throw directly an error if it is incorrect
 * @param name to be check
 * @param variableName to be checked
 * @throw an error if the format is not correct
 */
private fun checkNameFormat(name: String, variableName: String) {
    if (name.find { !it.isLetter() && it != '-' } != null || name.isEmpty()) {
        throw java.lang.Error("Incorrect $variableName")
    }
}

/**
 * Helper function to check if the email address is correct
 * @param email to be checked
 * @return true is the email is in the correct format, and false otherwise
 */
private fun checkEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * Helper function to check if the date of birth of the user respect the age condition of the app
 * @param date of the user birth
 * @throw an error if the age of the user give by the birth date don't respect the ge condition of the app
 */
private fun checkDateOfBirth(date: LocalDate) {
    if (!(date < LocalDate.now().plusYears(-10) && date > LocalDate.now().plusYears(-100))) {
        throw java.lang.Error("Incorrect date of birth !")
    }
}

/**
 * Helper function to check if the distance goal is greater or equal than zero
 * @param distanceGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkDistanceGoal(distanceGoal: Double) {
    if (distanceGoal <= 0.0) {
        throw java.lang.Error("The distance goal can't be equal or less than 0.")
    }
}

/**
 * Helper function to check if the activity time goal is greater or equal than zero
 * @param activityTimeGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkActivityTimeGoal(activityTimeGoal: Double) {
    if (activityTimeGoal <= 0.0) {
        throw java.lang.Error("The activity time goal can't be equal or less than 0.")
    }
}

/**
 * Helper function to check if the number of paths goal is greater or equal than zero
 * @param nbOfPathsGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkNbOfPathsGoal(nbOfPathsGoal: Int) {
    if (nbOfPathsGoal <= 0) {
        throw java.lang.Error("The number of paths goal can't be equal or less than 0.")
    }
}



