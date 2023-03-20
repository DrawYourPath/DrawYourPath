package com.epfl.drawyourpath.userProfile

import com.epfl.drawyourpath.authentication.Auth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.Database
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class UserModel {
    //the user authenticate in the app
    private val userAuth: User
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
    private var friendsList: HashMap<String, String> //(username, userId)


    /**
     * THis constructor will create a new user based on the user model of the app
     * @param userAuth user authenticate give by the login
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @throws error if the inputs are incorrect
     */
    constructor(userAuth: User, username: String, firstname: String, surname: String, dateOfBirth: LocalDate, distanceGoal: Double, activityTimeGoal: Double, nbOfPathsGoal: Int, database: Database){
        this.database = database
        this.userAuth = userAuth

        //obtain the userId and the email give by the authentication
        this.userId=userAuth.getUid()
        this.emailAddress=userAuth.getEmail()

        //check the username
        if(username.isEmpty()){
            throw java.lang.Error("The username can't be empty !")
        }
        if(!checkUsername(userId,username, database)){
            throw java.lang.Error("The username not correspond to the given userId !")
        }
        this.username=username

        //check the format of the firstname
        if(!checkNameFormat(firstname)){
            throw java.lang.Error("Incorrect firstname")
        }else{
            this.firstname = firstname
        }

        //check the format of the surname
        if(!checkNameFormat(surname)){
            throw java.lang.Error("Incorrect surname")
        }else {
            this.surname = surname
        }

        //check that the birth date respect the age condition of the app(10<=age<=100)
        if(!checkDateOfBirth(dateOfBirth)){
            throw java.lang.Error("Incorrect date of birth !")
        }else{
            this.dateOfBirth=dateOfBirth
        }

        //test the goals, the goals can't be equal to 0
        if(distanceGoal==0.0){
            throw java.lang.Error("The distance goal can't be equal to 0.")
        }
        this.distanceGoal=distanceGoal
        if(activityTimeGoal==0.0){
            throw java.lang.Error("The activity time goal can't be equal to 0.")
        }
        this.activityTimeGoal=activityTimeGoal
        if(nbOfPathsGoal==0){
            throw java.lang.Error("The number of paths goal can't be equal to 0.")
        }
        this.nbOfPathsGoal=nbOfPathsGoal

        this.friendsList = HashMap()
        //TODO: in the next task, the firstname, surname, dateOfBirth and the goals will be set to the database here
    }

    /**
     * Get the userId of the user
     * @return userId of the user
     */
    fun getUserId(): String{
        return userId
    }

    /**
     * Get the username of the user
     * @return username of the user
     */
    fun getUsername(): String{
        return username
    }

    /**
     * Use this function to modify the username(the username will be modify only if it is available on the database)
     * @param username that we want to set
     */
    fun setUsername(username: String){
        database.updateUsername(username, userId).thenAccept{
            if(it){
                this.username=username
            }
        }
    }
    /**
     * Get the email address of the user
     * @return email address of the user
     */
    fun getEmailAddress(): String{
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
    fun getFirstname(): String{
        return firstname
    }

    /**
     * Get the surname of the user
     * @return surname of the user
     */
    fun getSurname(): String{
        return surname
    }

    /**
     * Get the date of birth of the user
     * @return date of birth of the user
     */
    fun getDateOfBirth(): LocalDate{
        return dateOfBirth
    }

    /**
     * Get the daily distance goal of the user
     * @return daily distance goal of the user
     */
    fun getDistanceGoal(): Double{
        return distanceGoal
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distance new daily distance goal
     */
    fun setDistanceGoal(distance: Double){
        if(distance <= 0.0){
            throw java.lang.Error("The distance goal can't be equal to 0 !")
        }
        this.distanceGoal=distance
    }

    /**
     * Get the daily activity time goal of the user
     * @return daily activity time goal of the user
     */
    fun getActivityTime(): Double{
        return activityTimeGoal
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param time new daily activity time goal
     */
    fun setActivityTimeGoal(time: Double){
        if(time <= 0.0){
            throw java.lang.Error("The activity time goal can't be equal to 0 !")
        }
        this.activityTimeGoal=time
    }

    /**
     * Get the daily number of paths goal of the user
     * @return daily number of paths goal of the user
     */
    fun getNumberOfPathsGoal(): Int{
        return nbOfPathsGoal
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPaths new daily number of paths goal
     */
    fun setNumberOfPathsGoal(nbOfPaths: Int){
        if(nbOfPaths <= 0){
            throw java.lang.Error("The number of paths goal can't be equal to 0 !")
        }
        this.nbOfPathsGoal=nbOfPaths
    }

    /**
     * Get the age of the user
     * @return the age of the user
     */
    fun getAge(): Int{
        var age = LocalDate.now().year-dateOfBirth.year
        if(LocalDate.now().minusYears(age.toLong())<=dateOfBirth){
            age -= 1
        }
        return age
    }

    /**
     * This function will remove the user with username to the friend list
     * @param username of the user that we want to remove
     */
    fun removeFriend(username: String) {
        if(!friendsList.contains(username)) {
            throw java.lang.Error("This user is not in the friend list !")
        }
        friendsList.remove(username)
    }

    /**
     * This function will add the user with username to the friend list. To be added the user must be present in the database.
     * @param username of the user that we want to add to the friend list
     */
    fun addFriend(username: String) {
        //TODO:this function will be implemented during a next task when the database will be cleaned
    }

    /**
     * This function will return the friend list of a user
     * @return the friend list of the user
     */
    fun getFriendList():Map<String, String> {
        return this.friendsList
    }

}

/**
 * Helper function to check the userId and affect it if it's correct
 * @param userId that correspond to the user
 * @param database where the userId should be present
 */
private fun checkUserId(userId: String, database: Database):Boolean{
    return database.isUserStoredInDatabase(userId).get(10, TimeUnit.SECONDS)
}
/**
 * Helper function to check the username and affect it if it's correct
 * @param userId that correspond to the user
 * @param username associated to the user profile
 * @param database where the userId should be present
 */
private fun checkUsername(userId: String, username: String, database: Database): Boolean{
    //TODO: Will be change in my next task when change the database organization
    return !database.isUsernameAvailable(username).get(10, TimeUnit.SECONDS)
}
/**
 * Helper function to check that the firstname and the surname respect the name format condition of the app
 * @param name to be checked
 * @return true if the name is in the correct format, and false otherwise
 */
private fun checkNameFormat(name: String): Boolean{
    if(name.find { !it.isLetter() && it != '-' } != null || name.isEmpty()){
        return false
    }
    return true
}

/**
 * Helper function to check if the email address is correct
 * @param email to be checked
 * @return true is the email is in the correct format, and false otherwise
 */
private fun checkEmail(email: String):Boolean {
    // check for @ char
    var atSymbol = email.indexOf("@")
    if(atSymbol < 1) {
        return false
    }
    //test if the email contain only one @
    if(email.indexOf("@", atSymbol+1)!= -1){
        return false
    }

    var dot = email.indexOf(".", atSymbol)

    // check that the dot is not at the end
    if (dot === email.length - 1) {
        return false
    }

    return true
}
/**
 * Helper function to check the date of birth of the user
 * @param date of the user birth
 * @return return true if the user is aged between 10 and 100 years and false otherwise
 */
private fun checkDateOfBirth(date: LocalDate): Boolean{
    return date !== null &&
            date < LocalDate.now().plusYears(-10) &&
            date > LocalDate.now().plusYears(-100)
}



