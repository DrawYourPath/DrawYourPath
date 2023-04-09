package com.epfl.drawyourpath.userProfile.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.ByteArrayOutputStream
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
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    val profilePhoto: ByteArray?
) {

    /**
     * Get the age of the user
     * @return the age of the user
     */
    fun getAge(): Int {
        return ChronoUnit.YEARS.between(getDateOfBirthAsLocalDate(), LocalDate.now()).toInt()
    }

    /**
     * get the profile photo of the user
     * @return the profile photo of the user
     */
    fun getProfilePhotoAsBitmap(): Bitmap? {
        if (profilePhoto == null) {
            return null
        }
        return BitmapFactory.decodeByteArray(profilePhoto, 0, profilePhoto.size, BitmapFactory.Options())
    }

    /**
     * get the date of birth of the user
     * @return the date of birth of the user
     */
    fun getDateOfBirthAsLocalDate(): LocalDate {
        return LocalDate.ofEpochDay(dateOfBirth)
    }

    companion object {
        fun fromBitmapToByteArray(image: Bitmap?): ByteArray? {
            if (image == null) {
                return null
            }
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            return stream.toByteArray()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

}
