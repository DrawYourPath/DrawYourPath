package com.epfl.drawyourpath.userProfile

import android.graphics.Color
import android.text.TextUtils
import com.epfl.drawyourpath.database.Database
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class UserModel {
    //the userId is given by the authentication part and is unique to the user
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

    /**
     * THis constructor will create a new user based on the user model of the app
     * @param userId is given by the authentification part and is unique to the user (used to access the user profile on the database)
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @throws an error if the inputs are incorrect
     */
    constructor(userId: String, username: String, emailAddress: String, firstname: String, surname: String, dateOfBirth: LocalDate, distanceGoal: Double, activityTimeGoal: Double, nbOfPathsGoal: Int, database: Database){
        this.database = database

        //check the userId
        if(userId.isEmpty()){
            throw java.lang.Error("The userId can't be empty !")
        }
        if(!checkUserId(userId,database)){
            throw java.lang.Error("The userId must be present on the database !")
        }
        this.userId=userId

        //check the username
        if(username.isEmpty()){
            throw java.lang.Error("The username can't be empty !")
        }
        if(!checkUsername(userId,username, database)){
            throw java.lang.Error("The username not correspond to the given userId !")
        }
        this.username=username

        //check the format of the mail
        if(!checkEMail(emailAddress)){
            throw java.lang.Error("The mail address is not in the correct format !")
        }else{
            this.emailAddress = emailAddress
        }

        //check the format of the firstname
        if(!checkName(firstname)){
            throw java.lang.Error("Incorrect firstname")
        }else{
            this.firstname = firstname
        }

        //check the format of the surname
        if(!checkName(surname)){
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

    /**
     * Use this function to modify the email address of the user
     * @param email new email address
     */
    fun setEmailAddress(email: String){
        if(!checkEMail(email)){
            throw java.lang.Error("Invalid email format !")
        }
        this.emailAddress=email
    }

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
        if(distance == 0.0){
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
        if(time == 0.0){
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
        if(nbOfPaths == 0){
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
}

/**
 * Helper function to check the userId and affect it if it's correct
 * @param userId that correspond to the user
 * @param database where the userId should be present
 */
private fun checkUserId(userId: String, database: Database):Boolean{
    return database.isUserStoreOnDatabase(userId).get(10, TimeUnit.SECONDS)
}
/**
 * Helper function to check the username and affect it if it's correct
 * @param userId that correspond to the user
 * @param username associated to the user profile
 * @param database where the userId should be present
 */
private fun checkUsername(userId: String, username: String, database: Database): Boolean{
    //TODO: Will be change in my next task when change the database organization
    return !database.isUserNameAvailable(username).get(10, TimeUnit.SECONDS)
}
/**
 * Helper function to check that the firstname and the surname respect the name format condition of the app
 * @param name to be checked
 * @return true if the name is in the correct format, and false otherwise
 */
private fun checkName(name: String): Boolean{
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
private fun checkEMail(email: String):Boolean {
    // check for @ char
    var atSymbol = email.indexOf("@")
    if(atSymbol < 1) {
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
    if(date == null){
        return false
    }
    //check if the date of birth respect the age conditions
    val todayDate = LocalDate.now()
    val minTodayAge: LocalDate =
        LocalDate.of(todayDate.year - 10, todayDate.monthValue, todayDate.dayOfMonth)
    val maxTodayAge: LocalDate =
        LocalDate.of(todayDate.year - 100, todayDate.monthValue, todayDate.dayOfMonth)
    if (date < maxTodayAge || date > minTodayAge) {
        return false
    }
    return true
}



