package com.epfl.utils.drawyourpath

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmap
import com.epfl.drawyourpath.R
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * A class containing various utility functions.
 */
object Utils {
    /**
     * A function that generates a static map URL with the user's run data
     */
    fun getStaticMapUrl(runCoordinates: List<LatLng>, apiKey: String): String {
        var path = "path=color:0x0000ff|weight:5"
        for (coordinate in runCoordinates) {
            path += "|${coordinate.latitude},${coordinate.longitude}"
        }

        val url = "https://maps.googleapis.com/maps/api/staticmap?size=80x80&maptype=roadmap&$path&key=$apiKey"
        return url
    }

    fun <T> failedFuture(throwable: Throwable): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        future.completeExceptionally(throwable)
        return future
    }

    /**
     * get the profile photo as bitmap or if none the default profile photo
     * @param photo the photo to decode
     * @param res the resources used to get the default profile photo
     * @return the profile photo or the default profile photo
     */
    fun decodePhotoOrGetDefault(photo: ByteArray?, res: Resources): Bitmap {
        if (photo == null) {
            return getDefaultPhoto(res)
        }
        return BitmapFactory.decodeByteArray(photo, 0, photo.size, BitmapFactory.Options()) ?: getDefaultPhoto(res)
    }

    /**
     * Decodes the photo from base64 string to bitmap format
     * @param photoStr photo encoded
     * @return the photo in bitmap format
     */
    fun decodePhotoOrGetDefault(photoStr: String?, res: Resources): Bitmap {
        return decodePhotoOrGetDefault(decodeStringAsByteArray(photoStr), res)
    }

    /**
     * Decodes the photo from base64 string to bitmap format and return null if the dataSnapShot is null
     * @param photoStr photo encoded
     * @return the photo in bitmap format, and null if no photo is stored on the database
     */
    fun decodePhoto(photoStr: String): Bitmap {
        val tabByte = Base64.getDecoder().decode(photoStr)
        return BitmapFactory.decodeByteArray(tabByte, 0, tabByte.size)
    }

    /**
     * get the default profile photo
     * @param res the resources
     * @return the default profile photo
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDefaultPhoto(res: Resources): Bitmap {
        return res.getDrawable(R.drawable.profile_placholderpng, null).toBitmap()
    }

    /**
     * Decodes the string from base64 to Bytearray
     * @param string the encoded string
     * @return the corresponding byte array
     */
    fun decodeStringAsByteArray(string: String?): ByteArray? {
        return string?.let { Base64.getDecoder().decode(it) }
    }

    /**
     * Encodes a bitmap image as a byte array.
     * @param photo the bitmap
     * @param quality the quality of the encoding
     * @return the byteArray of the photo
     */
    fun encodePhotoToByteArray(photo: Bitmap, quality: Int = 50): ByteArray {
        val byteArray = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.WEBP, quality, byteArray)
        return byteArray.toByteArray()
    }

    /**
     * Encodes a bitmap image.
     * @return A base64 string of the image.
     */
    fun encodePhotoToString(photo: Bitmap, quality: Int = 50): String {
        return Base64.getEncoder().encodeToString(encodePhotoToByteArray(photo, quality))
    }

    /**
     * Helper function to check if the name format of a given variableName is correct and throw directly an error if it is incorrect
     * @param name to be check
     * @param variableName to be checked
     * @throw an error if the format is not correct
     */
    fun checkNameFormat(name: String, variableName: String) {
        if (name.find { !it.isLetter() && it != '-' } != null || name.isEmpty()) {
            throw Error("Incorrect $variableName \"$name\"")
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
            throw Error("Incorrect date of birth !")
        }
    }

    /**
     * Helper function to check if the goals are greater or equal than zero
     * @param distanceGoal to be checked
     * @param activityTimeGoal to be checked
     * @param nbOfPathsGoal to be checked
     * @throw an error if the goal is incorrect
     */
    fun checkGoals(distanceGoal: Double? = null, activityTimeGoal: Double? = null, nbOfPathsGoal: Int? = null) {
        if (distanceGoal != null && distanceGoal <= 0.0) {
            throw Error("The distance goal can't be equal or less than 0.")
        }
        if (activityTimeGoal != null && activityTimeGoal <= 0.0) {
            throw Error("The activity time goal can't be equal or less than 0.")
        }
        if (nbOfPathsGoal != null && nbOfPathsGoal <= 0) {
            throw Error("The number of paths goal can't be equal or less than 0.")
        }
    }
}
