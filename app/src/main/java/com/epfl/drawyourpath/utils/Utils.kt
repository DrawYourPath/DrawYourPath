package com.epfl.drawyourpath.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import androidx.core.graphics.drawable.toBitmap
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.UserGoals
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.Ink.Point
import com.google.mlkit.vision.digitalink.Ink.Stroke
import java.io.ByteArrayOutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.*

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
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes(), duration.seconds % 60)
    }

    /**
     * Helper function to get a string to displayed the start time and end time in "hh:mm:ss"
     * @param time that we want to displayed in seconds
     * @return the string that correspond to the time in "hh:mm:ss"
     */
    fun getStringTimeStartEnd(time: Long): String =
        LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
     * Gets the current epoch as a Long.
     * @return The current epoch.
     */
    fun getCurrentDateAsEpoch(): Long {
        return LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
    }

    /**
     * Converts a list of LatLng to a stroke.
     * @param coordinates The coordinates we want to convert
     * @return A Stroke object representing the coordinates in planar space.
     */
    fun coordinatesToStroke(coordinates: List<LatLng>): Stroke {
        val builder = Stroke.builder()

        for (coordinate in coordinates) {
            builder.addPoint(coordinateToPoint(coordinate))
        }

        return builder.build()
    }

    /**
     * Converts a LatLng to a Point.
     * @param coordinate The coordinate we want to convert
     * @return A Point object representing the coordinate in planar space.
     */
    fun coordinateToPoint(coordinate: LatLng): Point {
        val lat = Math.toRadians(coordinate.latitude)
        val long = Math.toRadians(coordinate.longitude)
        // Mercator projection formula
        val y = ln(tan(lat) + (1 / cos(lat)))
        return Point.create(long.toFloat(), y.toFloat())
    }

    /**
     * Gets the smallest coordinates independently.
     * When we want to fit the path in a square, it represents the topleft corner of the square.
     * @return The smallest coordinate for all the strokes.
     */
    fun getSmallestPoint(strokes: List<Stroke>): Point =
        strokes.fold(Pair(Float.MAX_VALUE, Float.MAX_VALUE)) { acc, stroke ->
            stroke.points.fold(acc) { acc2, point ->
                Pair(min(acc2.first, point.x), min(acc2.second, point.y))
            }
        }.let { Point.create(it.first, it.second) }

    /**
     * Gets the smallest coordinates independently.
     * When we want to fit the path in a square, it represents the topleft corner of the square.
     * @return The smallest coordinate for the stroke.
     */
    fun getSmallestPoint(stroke: Stroke): Point =
        getSmallestPoint(listOf(stroke))

    /**
     * Gets the biggest coordinates independently.
     * When we want to fit the path in a square, it represents the bottom right corner of the square.
     * @return The smallest coordinate for all the strokes.
     */
    fun getBiggestPoint(strokes: List<Stroke>): Point =
        strokes.fold(Pair(0f, 0f)) { acc, stroke ->
            stroke.points.fold(acc) { acc2, point ->
                Pair(max(acc2.first, point.x), max(acc2.second, point.y))
            }
        }.let { Point.create(it.first, it.second) }

    /**
     * Gets the smallest coordinates independently.
     * When we want to fit the path in a square, it represents the bottom right corner of the square.
     * @return The biggest coordinate for the stroke.
     */
    fun getBiggestPoint(stroke: Stroke): Point =
        getBiggestPoint(listOf(stroke))

    /**
     * Normalizes strokes so that it fits in a 1:1 square.
     * @note It isn't stretched.
     * @param strokes The strokes to normalize.
     * @param padding The padding applied between the borders and the points in percent.
     * @return All the strokes with all points inside fitting in [0;1].
     */
    fun normalizeStrokes(strokes: List<Stroke>, padding: Float = 0f): List<Stroke> {
        // Finds the origin that fits all the points. (top left most corner)
        val origin = getSmallestPoint(strokes)

        // Finds the biggest point (opposite of origin). (bottom right most corner)
        val max = getBiggestPoint(strokes)

        // The scale is the biggest distance between the origin and a coordinate.
        val scale = max(max.x - origin.x, max.y - origin.y) + padding

        // We pad the origin relatively to the scaling.
        val paddedOrigin = Pair(
            origin.x - scale * padding,
            origin.y - scale * padding,
        )

        // Constructs the new strokes translated and scaled to fit in a 1:1 square
        return strokes.map { stroke ->
            Stroke.builder().also {
                for (point in stroke.points) {
                    it.addPoint(
                        Point.create(
                            (point.x - paddedOrigin.first) / scale,
                            (point.y - paddedOrigin.second) / scale,
                        ),
                    )
                }
            }.build()
        }
    }

    // Defaults paint used to draw strokes.
    // Uses anti aliasing and draws rounded black strokes of width 2.
    private val defaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.BLACK
        it.strokeWidth = 2f
        it.strokeCap = Paint.Cap.ROUND
    }

    /**
     * Converts a stroke to a bitmap image representation.
     * @param stroke The stroke we want to draw
     * @param size The size of the bitmap in pixels
     * @param paint The paint option used to draw the stroke.
     */
    fun strokesToBitmap(strokes: List<Stroke>, size: Int = 100, paint: Paint = defaultPaint): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        normalizeStrokes(strokes, 0.1f).forEach { stroke ->
            val points = stroke.points

            // Associates idx (n) to (n + 1)
            points.zip(points.drop(1)).forEach {
                canvas.drawLine(it.first.x * size, it.first.y * size, it.second.x * size, it.second.y * size, paint)
            }
        }

        return bitmap
    }

    /**
     * Converts a list of coordinates to a bitmap image representation.
     * @param stroke The list of coordinates we want to draw
     * @param size The size of the bitmap in pixels
     * @param paint The paint option used to draw the list of coordinates.
     */
    fun coordinatesToBitmap(coordinates: List<LatLng>, size: Int = 100, paint: Paint = defaultPaint): Bitmap {
        return strokesToBitmap(listOf(coordinatesToStroke(coordinates)), size, paint)
    }
}
