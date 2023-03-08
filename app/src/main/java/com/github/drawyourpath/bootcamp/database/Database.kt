package com.github.drawyourpath.bootcamp.database

import android.widget.EditText
import android.widget.TextView

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
     * @param outputText
     */
    abstract fun setUserName(userName: String, outputText: TextView)
}