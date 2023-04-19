package com.epfl.drawyourpath.userProfile

import android.graphics.Bitmap
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

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
    private var currentDistanceGoal: Double

    //the activity time goal is initialize at the profile creation and can be modify
    private var currentActivityTimeGoal: Double

    //the number of path goal is initialize at the profile creation and can be modify
    private var currentNbOfPathsGoal: Int

    //list of daily goals realized by the user
    private var dailyGoalList: List<DailyGoal>

    //database where the user is store online
    private var database: Database

    //friend list
    private var friendsList: List<String> //a list of userId

    //profile photo, can be null if the user don't want to
    private var profilePhoto: Bitmap? = null

    //user achievements
    //total distance run by the user
    private var totalDistance: Double

    //total activity time of the user
    private var totalActivityTime: Double

    //total number of paths draw by the user
    private var totalNbOfPaths: Int


    /**
     * THis constructor will create a new user based on the user model of the app(constructor at the the profile creation)
     * @param userAuth user authenticate give by the login
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @param dailyGoalList a list of daily goal realized by the user (by default the list is empty)
     * @param totalDistance total distance run by the user since the creation of his profile(default value is 0)
     * @param totalActivityTime total activity time since the creation of the his profile(default value is 0)
     * @param totalNbOfPaths total number of paths draw by the user since the creation of his profile
     * @throws error if the inputs are incorrect
     */
    constructor(
        userAuth: User, username: String, firstname: String, surname: String, dateOfBirth: LocalDate, distanceGoal: Double,
        activityTimeGoal: Double, nbOfPathsGoal: Int, database: Database, dailyGoalList: List<DailyGoal> = emptyList(),
        totalDistance: Double = 0.0, totalActivityTime: Double = 0.0, totalNbOfPaths: Int = 0
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
        this.currentDistanceGoal = distanceGoal

        checkActivityTimeGoal(activityTimeGoal)
        this.currentActivityTimeGoal = activityTimeGoal

        checkNbOfPathsGoal(nbOfPathsGoal)
        this.currentNbOfPathsGoal = nbOfPathsGoal

        this.friendsList = ArrayList()
        this.profilePhoto = null
        this.dailyGoalList = dailyGoalList

        //init the user achievements
        this.totalDistance = totalDistance
        this.totalActivityTime = totalActivityTime
        this.totalNbOfPaths = totalNbOfPaths
    }

    /**
     * THis constructor will create a new user based on the user model of the app
     * @param userId of the user
     * @param emailAddress of the user
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @param friendsList the friendsList of the user(with a default empty friendsList)
     * @param dailyGoalList a list of daily goal realized by the user (by default the list is empty)
     * @param totalDistance total distance run by the user since the creation of his profile(default value is 0)
     * @param totalActivityTime total activity time since the creation of the his profile(default value is 0)
     * @param totalNbOfPaths total number of paths draw by the user since the creation of his profile
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
        database: Database,
        dailyGoalList: List<DailyGoal> = emptyList(),
        totalDistance: Double = 0.0,
        totalActivityTime: Double = 0.0,
        totalNbOfPaths: Int = 0
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
        this.currentDistanceGoal = distanceGoal

        checkActivityTimeGoal(activityTimeGoal)
        this.currentActivityTimeGoal = activityTimeGoal

        checkNbOfPathsGoal(nbOfPathsGoal)
        this.currentNbOfPathsGoal = nbOfPathsGoal

        this.friendsList = friendsList
        this.profilePhoto = profilePhoto
        this.dailyGoalList = dailyGoalList

        //init the user achievements
        this.totalDistance = totalDistance
        this.totalActivityTime = totalActivityTime
        this.totalNbOfPaths = totalNbOfPaths
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
    fun setUsername(username: String): CompletableFuture<Unit> {
        return database.updateUsername(username).thenApply {
            this.username = username
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
    fun getCurrentDistanceGoal(): Double {
        return currentDistanceGoal
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun setCurrentDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        checkDistanceGoal(distanceGoal)
        return database.setCurrentDistanceGoal(distanceGoal).thenApply {
            this.currentDistanceGoal = distanceGoal
            it
        }
    }

    /**
     * Get the daily activity time goal of the user
     * @return daily activity time goal of the user
     */
    fun getCurrentActivityTime(): Double {
        return currentActivityTimeGoal
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param time new daily activity time goal
     */
    fun setCurrentActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        checkActivityTimeGoal(activityTimeGoal)
        return database.setCurrentActivityTimeGoal(activityTimeGoal).thenApply {
            this.currentActivityTimeGoal = activityTimeGoal
            it
        }
    }

    /**
     * Get the daily number of paths goal of the user
     * @return daily number of paths goal of the user
     */
    fun getCurrentNumberOfPathsGoal(): Int {
        return currentNbOfPathsGoal
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPaths new daily number of paths goal
     */
    fun setCurrentNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        checkNbOfPathsGoal(nbOfPathsGoal)
        return database.setCurrentNbOfPathsGoal(nbOfPathsGoal).thenApply {
            this.currentNbOfPathsGoal = nbOfPathsGoal
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
            it
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
            it
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
    fun setProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        return database.setProfilePhoto(photo).thenApply {
            this.profilePhoto = photo
            it
        }
    }

    /**
     * This function will return a list of daily goal that the user have realized
     * @return a list of daily realized by the user
     */
    fun getDailyGoalList(): List<DailyGoal> {
        return this.dailyGoalList
    }

    /**
     * This function will add (or update it if a daily is already present at this date)a daily goal to the list of daily goal that the user has realized
     * @param dailyGoal that we want to add to the list of daily goals
     * @return a future that indicate if the daily has been correctly added
     */
    fun addDailyGoalToListOfDailyGoal(dailyGoal: DailyGoal): CompletableFuture<Unit> {
        return this.database.addDailyGoal(dailyGoal).thenApply { isSet ->
            val newDailyGoalList = this.dailyGoalList.filter { it.date != dailyGoal.date }.toMutableList()
            newDailyGoalList.add(dailyGoal)
            this.dailyGoalList = newDailyGoalList
            isSet
        }
    }

    /**
     * This function will return the total run by the user since the creation of his profile
     * @return the total distance run by the user
     */
    fun getTotalDistance(): Double {
        return this.totalDistance
    }

    /**
     * This function will return the total activity time that the user has made since the creation of his profile
     * @return the total activity time of the user
     */
    fun getTotalActivityTime(): Double {
        return this.totalActivityTime
    }

    /**
     * This function will return the total number of paths draw by the user since the creation of his profile
     * @return the total number of paths draw by the user
     */
    fun getTotalNbOfPaths(): Int {
        return this.totalNbOfPaths
    }

    /**
     * Function used to update the user achievements(total distance, total activity time and total nb of paths draw by the user)
     * with the result at the end of a drawing activity(remark: the total number of path will be incremented by one, since only one drw
     * can be achieved each drawing activity).
     * @param distanceDrawing distance run by user to achieve the drawing
     * @param activityTimeDrawing time take by the user to realized the drawing
     * @return a future that indicate if the achievements of the user have been correctly updated.
     */
    fun updateAchievements(distanceDrawing: Double, activityTimeDrawing: Double): CompletableFuture<Unit> {
        return this.database.updateUserAchievements(distanceDrawing, activityTimeDrawing).thenApply { isSet ->
            this.totalDistance += distanceDrawing
            this.totalActivityTime += activityTimeDrawing
            this.totalNbOfPaths += 1
            isSet
        }
    }

    companion object {
        /**
         * Helper function to check if the name format of a given variableName is correct and throw directly an error if it is incorrect
         * @param name to be check
         * @param variableName to be checked
         * @throw an error if the format is not correct
         */
        fun checkNameFormat(name: String, variableName: String) {
            if (name.find { !it.isLetter() && it != '-' } != null || name.isEmpty()) {
                throw java.lang.Error("Incorrect $variableName")
            }
        }

        /**
         * Helper function to check if the email address is correct
         * @param email to be checked
         * @return true is the email is in the correct format, and false otherwise
         */
        fun checkEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        /**
         * Helper function to check if the date of birth of the user respect the age condition of the app
         * @param date of the user birth
         * @throw an error if the age of the user give by the birth date don't respect the ge condition of the app
         */
        fun checkDateOfBirth(date: LocalDate) {
            if (!(date < LocalDate.now().plusYears(-10) && date > LocalDate.now().plusYears(-100))) {
                throw java.lang.Error("Incorrect date of birth !")
            }
        }

        /**
         * Helper function to check if the distance goal is greater or equal than zero
         * @param distanceGoal to be checked
         * @throw an error if the goal is incorrect
         */
        fun checkDistanceGoal(distanceGoal: Double) {
            if (distanceGoal <= 0.0) {
                throw java.lang.Error("The distance goal can't be equal or less than 0.")
            }
        }

        /**
         * Helper function to check if the activity time goal is greater or equal than zero
         * @param activityTimeGoal to be checked
         * @throw an error if the goal is incorrect
         */
        fun checkActivityTimeGoal(activityTimeGoal: Double) {
            if (activityTimeGoal <= 0.0) {
                throw java.lang.Error("The activity time goal can't be equal or less than 0.")
            }
        }

        /**
         * Helper function to check if the number of paths goal is greater or equal than zero
         * @param nbOfPathsGoal to be checked
         * @throw an error if the goal is incorrect
         */
        fun checkNbOfPathsGoal(nbOfPathsGoal: Int) {
            if (nbOfPathsGoal <= 0) {
                throw java.lang.Error("The number of paths goal can't be equal or less than 0.")
            }
        }
    }
}
