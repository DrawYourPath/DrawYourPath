package com.epfl.Utils.drawyourpath

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.time.*
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

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
     * Decodes the photo from base64 string to bitmap format and return null if the dataSnapShot is null
     * @param photoStr photo encoded
     * @return the photo in bitmap format, and null if no photo is stored on the database
     */
    fun decodePhoto(photoStr: String): Bitmap {
        val tabByte = Base64.getDecoder().decode(photoStr as String)
        return BitmapFactory.decodeByteArray(tabByte, 0, tabByte.size)
    }

    /**
     * Encodes a bitmap image.
     * @return A base64 string of the image.
     */
    fun encodePhoto(photo: Bitmap, quality: Int = 50): String {
        val byteArray = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.WEBP, quality, byteArray)
        return Base64.getEncoder().encodeToString(byteArray.toByteArray())
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
}
