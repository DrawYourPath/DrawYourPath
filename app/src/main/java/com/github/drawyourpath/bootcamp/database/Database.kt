package com.github.drawyourpath.bootcamp.database

import android.widget.TextView
import java.util.*

abstract class Database {
    /**
     * This function will return true if the userName is available for the user
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @param outputText text view used to indicate if the user name is available to the user
     * @return true if the userName was not already taken by another user
     */
    abstract fun isUserNameAvailable(userName: String, outputText: TextView): Boolean

    /**
     * This function will add the user Name to the database to create a new user profile
     * @param userName userName that the user want to set in the database
     * @param outputText text view used to indicate if the user name is available to the user,
     * in case of unavailability of the username
     * @return true if the username was set to the dataBase, otherwise false
     */
    abstract fun setUserName(userName: String, outputText: TextView) : Boolean

    /**
     * This function will set the personal info of a user to the database (firstname, surname, date of birth).
     * @param username username associated to the users(where the data will be affected)
     * @param firstname firstname of the user
     * @param surname surname of the user
     * @param dateOfBirth date of birth of the user
     */
    abstract  fun setPersonalInfo(username: String, firstname: String, surname: String, dateOfBirth: Date)
}