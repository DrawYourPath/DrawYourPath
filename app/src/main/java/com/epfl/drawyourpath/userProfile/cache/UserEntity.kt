package com.epfl.drawyourpath.userProfile.cache

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epfl.drawyourpath.R
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
    val goalAndProgress: GoalAndProgress = GoalAndProgress(),

    /**
     * the profile photo of the user (can be null)
     */
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    val profilePhoto: ByteArray? = null
) {

    /**
     * Get the age of the user
     * @return the age of the user
     */
    fun getAge(): Int {
        return ChronoUnit.YEARS.between(getDateOfBirthAsLocalDate(), LocalDate.now()).toInt()
    }

    /**
     * get the profile photo of the user or if none the default profile photo
     * @param res the resources used to get the default profile photo
     * @return the profile photo of the user or the default profile photo
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getProfilePhotoOrDefaultAsBitmap(res: Resources): Bitmap {
        if (profilePhoto == null) {
            return res.getDrawable(R.drawable.profile_placholderpng, null).toBitmap()
        }
        return BitmapFactory.decodeByteArray(profilePhoto, 0, profilePhoto.size, BitmapFactory.Options())
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
        /**
         * create a byteArray from a bitmap image
         * @param image the bitmap
         * @return the bytearray
         */
        fun fromBitmapToByteArray(image: Bitmap?): ByteArray? {
            if (image == null) {
                return null
            }
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.WEBP, 70, stream)
            return stream.toByteArray()
        }

        /**
         * create a Long from a LocalDate
         * @param date the localDate
         * @return the Long
         */
        fun fromLocalDateToLong(date: LocalDate): Long {
            return date.toEpochDay()
        }
    }

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
