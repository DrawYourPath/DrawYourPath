package com.epfl.drawyourpath.userProfile.cache

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epfl.drawyourpath.database.UserData
import com.epfl.drawyourpath.userProfile.UserProfile
import com.epfl.utils.drawyourpath.Utils

@Entity(tableName = "User")
data class UserEntity(
    /**
     * the id of the user
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val userId: String,

    /**
     * the username of the user
     */
    val username: String = "",

    /**
     * the email address of the user
     */
    @ColumnInfo(name = "email_address")
    val emailAddress: String = "",

    /**
     * the firstname of the user
     */
    val firstname: String = "",

    /**
     * the surname of the user
     */
    val surname: String = "",

    /**
     * the date of birth of the user
     */
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Long = 0L,

    /**
     * the current goals and total progress done by the user
     */
    @Embedded
    val goalAndAchievements: GoalAndAchievements = GoalAndAchievements(),

    /**
     * the profile photo of the user (can be null)
     */
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    val profilePhoto: ByteArray? = null,
) {
    constructor(userData: UserData, alternativeUserId: String) : this(
        userData.userId ?: alternativeUserId,
        userData.username ?: "",
        userData.email ?: "",
        userData.firstname ?: "",
        userData.surname ?: "",
        userData.birthDate ?: 0,
        userData.goals?.let { GoalAndAchievements(it) } ?: GoalAndAchievements(),
        Utils.decodeStringAsByteArray(userData.picture),
    )

    constructor(userProfile: UserProfile) : this(
        userProfile.userId,
        userProfile.username,
        userProfile.emailAddress,
        userProfile.firstname,
        userProfile.surname,
        userProfile.birthDate.toEpochDay(),
        GoalAndAchievements(userProfile.goals),
        null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }
}
