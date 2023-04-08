package com.epfl.drawyourpath.userProfile.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity(tableName = "User")
data class UserData(
    /**
     * the id of the user
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val userId: String,

    /**
     * the username of the user
     */
    val username: String,

    /**
     * the email address of the user
     */
    @ColumnInfo(name = "email_address")
    val emailAddress: String,

    /**
     * the firstname of the user
     */
    val firstname: String,

    /**
     * the surname of the user
     */
    val surname: String,

    /**
     * the date of birth of the user
     */
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Long,

    /**
     * the distance goal of the user
     */
    @ColumnInfo(name = "distance_goal")
    val distanceGoal: Double,

    /**
     * the activity time goal of the user
     */
    @ColumnInfo(name = "time_goal")
    val activityTimeGoal: Double,

    /**
     * the number of paths goal of the user
     */
    @ColumnInfo(name = "paths_goal")
    val nbOfPathsGoal: Int,

    /**
     * the profile photo of the user (can be null)
     */
    /*@ColumnInfo(name = "photo")
    val profilePhoto: Bitmap?*/
) {

    /**
     * Get the age of the user
     * @return the age of the user
     */
    fun getAge(): Int {
        return ChronoUnit.YEARS.between(getDateOfBirthAsLocalDate(), LocalDate.now()).toInt()
    }

    /**
     * get the date of birth of the user
     * @return the date of birth of the user
     */
    fun getDateOfBirthAsLocalDate(): LocalDate {
        return LocalDate.ofEpochDay(dateOfBirth)
    }

}
