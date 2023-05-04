package com.epfl.drawyourpath.userProfile

import android.content.res.Resources
import android.graphics.Bitmap
import com.epfl.drawyourpath.database.UserData
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.userProfile.cache.GoalAndAchievements
import com.epfl.drawyourpath.userProfile.cache.UserEntity
import com.epfl.utils.drawyourpath.Utils
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class UserProfile(
    /**
     * the id of the user
     */
    val userId: String,

    /**
     * the username of the user
     */
    val username: String,

    /**
     * the email address of the user
     */
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
    val birthDate: LocalDate,

    /**
     * the current goals and total progress done by the user
     */
    val goals: Goals,

    /**
     * the profile photo of the user
     */
    val profilePhoto: (res: Resources) -> Bitmap,

    /**
     * the age of the user
     */
    val age: Int = ChronoUnit.YEARS.between(birthDate, LocalDate.now()).toInt(),
) {

    constructor(userEntity: UserEntity) : this(
        userEntity.userId,
        userEntity.username,
        userEntity.emailAddress,
        userEntity.firstname,
        userEntity.surname,
        LocalDate.ofEpochDay(userEntity.dateOfBirth),
        Goals(userEntity.goalAndAchievements),
        { Utils.decodePhotoOrGetDefault(userEntity.profilePhoto, it) },
    )

    constructor(userData: UserData) : this(
        userData.userId ?: "ID_INVALID",
        userData.username ?: "USERNAME_INVALID",
        userData.email ?: "EMAIL_INVALID",
        userData.firstname ?: "FIRSTNAME_INVALID",
        userData.surname ?: "SURNAME_INVALID",
        userData.birthDate?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now(),
        userData.goals?.let { Goals(it) } ?: Goals(0.0, 0.0, 0),
        { Utils.decodePhotoOrGetDefault(userData.picture, it) },
    )

    data class Goals(
        /**
         * the distance goal of the user
         */
        val distanceGoal: Double,

        /**
         * the activity time goal of the user
         */
        val activityTimeGoal: Double,

        /**
         * the number of paths goal of the user
         */
        val pathsGoal: Int,
    ) {
        constructor(goalAndAchievements: GoalAndAchievements) : this(
            goalAndAchievements.distanceGoal,
            goalAndAchievements.activityTimeGoal,
            goalAndAchievements.nbOfPathsGoal,
        )

        constructor(userGoals: UserGoals) : this(
            userGoals.distance ?: 0.0,
            userGoals.activityTime?.toDouble() ?: 0.0,
            userGoals.paths?.toInt() ?: 0,
        )
    }
}
