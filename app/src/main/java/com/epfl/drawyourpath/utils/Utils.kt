package com.epfl.drawyourpath.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmap
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.UserGoals
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.Ink.Point
import com.google.mlkit.vision.digitalink.Ink.Stroke
import java.io.ByteArrayOutputStream
import java.time.*
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.tan

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
        return photo?.let { decodePhoto(photo) } ?: getDefaultPhoto(res)
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
     * Decodes the photo from byte array to bitmap format
     * @param photo photo encoded
     * @return the photo in bitmap format
     */
    fun decodePhoto(photo: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(photo, 0, photo.size)
    }

    /**
     * Decodes the photo from base64 string to bitmap format
     * @param photoStr photo encoded
     * @return the photo in bitmap format
     */
    fun decodePhoto(photoStr: String): Bitmap? {
        return decodePhoto(decodeStringAsByteArray(photoStr)!!)
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
     * @param goals to be checked
     * @throw an error if the goal is incorrect
     */
    fun checkGoals(goals: UserGoals) {
        if (goals.distance != null && goals.distance <= 0.0) {
            throw Error("The distance goal can't be equal or less than 0.")
        }
        if (goals.activityTime != null && goals.activityTime <= 0.0) {
            throw Error("The activity time goal can't be equal or less than 0.")
        }
        if (goals.paths != null && goals.paths <= 0) {
            throw Error("The number of paths goal can't be equal or less than 0.")
        }
    }

    /**
     * Get current date and time in epoch seconds
     * @return current date and time in epoch seconds
     */
    fun getCurrentDateTimeInEpochSeconds(): Long {
        return LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
    }

    /**
     * Helper function to get a string to displayed a distance in kilometer
     * @param distance that we want to displayed in meters
     * @return the string that correspond to the distance in kilometers
     */
    fun getStringDistance(distance: Double): String {
        // convert m to km
        val roundDistance: Double = (distance / 10.0).roundToInt() / 100.0
        return roundDistance.toString()
    }

    /**
     * Helper function to get a string to displayed a time duration in "hh:mm:ss"
     * @param time that we want to displayed in seconds
     * @return the string that correspond to the time in "hh:mm:ss"
     */
    fun getStringDuration(time: Long): String {
        val duration = Duration.ofSeconds(time)
        val hours: Int = duration.toHours().toInt()
        val hoursStr: String = if (hours == 0) "00" else if (hours < 10) "0$hours" else hours.toString()
        val minutes: Int = duration.toMinutes().toInt() - hours * 60
        val minutesStr: String = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes.toString()
        val seconds: Int = duration.seconds.toInt() - 3600 * hours - 60 * minutes
        val secondsStr: String = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds.toString()

        return "$hoursStr:$minutesStr:$secondsStr"
    }

    /**
     * Helper function to get a string to displayed the start time and end time in "hh:mm:ss"
     * @param time that we want to displayed in seconds
     * @return the string that correspond to the time in "hh:mm:ss"
     */
    fun getStringTimeStartEnd(time: Long): String {
        val localTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
        val hours = localTime.hour
        val hoursStr = if (hours == 0) "00" else if (hours < 10) "0$hours" else hours
        val minutes = localTime.minute
        val minutesStr = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes
        val seconds = localTime.second
        val secondsStr = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds
        return "$hoursStr:$minutesStr:$secondsStr"
    }

    /**
     * Helper function to get a string to displayed a speed in m/s
     * @param speed that we want to displayed in m/s
     * @return the string that correspond to the speed in m/s
     */
    fun getStringSpeed(speed: Double): String {
        val roundSpeed: Double = (speed * 100.0).roundToInt() / 100.0
        return roundSpeed.toString()
    }

    /**
     * Converts a list of LatLng to a stroke.
     * @param coordinates The coordinates we want to convert
     * @return A Stroke object representing the coordinates in planar space.
     */
    fun coordinatesToInk(coordinates: List<LatLng>): Stroke {
        val builder = Stroke.builder()

        for (coordinate in coordinates) {
            builder.addPoint(coordinateToInk(coordinate))
        }

        return builder.build()
    }

    /**
     * Converts a LatLng to a Point.
     * @param coordinate The coordinate we want to convert
     * @return A Point object representing the coordinate in planar space.
     */
    fun coordinateToInk(coordinate: LatLng): Point {
        val lat = Math.toRadians(coordinate.latitude)
        val long = Math.toRadians(coordinate.longitude)
        // Mercator projection formula
        val y = ln(tan(lat) + (1 / cos(lat)))
        return Point.create(long.toFloat(), y.toFloat())
    }
}
