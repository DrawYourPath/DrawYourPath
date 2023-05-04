package com.epfl.drawyourpath.path

import com.google.firebase.database.Exclude
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A class representing a run taken by a user.
 * The run includes information such as the path taken, distance, duration, timestamp,
 * user, averageSpeed, and calorie burn.
 * Various methods are included to calculate and retrieve this information.
 */
class Run(
    // val user: User,             //TODO add later
    private val path: Path, // represents the path taken by the user
    private val startTime: Long, // the timestamps of the run
    private val endTime: Long,
) {

    init {
        if (endTime <= startTime) {
            throw IllegalArgumentException("End time must be greater than start time")
        }
        calculateDistance()
        calculateDuration()
        calculateAverageSpeed()
        calculateTimeForOneKilometer()
        calculateCalorieBurn()
    }

    // the distance of the run (in meters)
    private var distance: Double = 0.0

    // the duration of the run (in seconds)
    private var duration: Long = 0L

    // the average speed of the run (in meters per second)
    private var averageSpeed: Double = 0.0

    // the calorie burn of the run
    private var calories: Int = 0

    // time it took to run 1km (in seconds)
    private var timeForOneKilometer: Long = 0L

    private fun calculateTimeForOneKilometer() {
        timeForOneKilometer = (1000 / averageSpeed).toLong()
    }

    private fun calculateAverageSpeed() {
        averageSpeed = distance / duration
    }

    private fun calculateDuration() {
        duration = endTime - startTime
    }

    /**
     * Calculates the calorie burn of the run based on the characteristics of the user.(not yes implemented)
     */
    fun calculateCalorieBurn() {
        calories = distance.toInt()
        // TODO implement later based on the characteristics of the user
    }

    private fun calculateDistance() {
        this.distance = path.getDistance()
    }

    /**
     * Returns the distance of the run (in meters)
     */
    @Exclude
    fun getDistance(): Double {
        return distance
    }

    /**
     * Returns the duration of the run (in seconds)
     */
    @Exclude
    fun getDuration(): Long {
        return duration
    }

    /**
     * Returns the start and end time of the run (in seconds)
     */
    fun getStartTime(): Long {
        return startTime
    }

    /**
     * Returns the date of the run as a string
     */
    @Exclude
    fun getDate(): String {
        val date = LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.UTC)
        return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"))
    }

    /**
     * Returns the end time of the run (in seconds)
     */
    fun getEndTime(): Long {
        return endTime
    }

    /**
     * Returns the average speed of the run (in meters per second)
     */
    @Exclude
    fun getAverageSpeed(): Double {
        return averageSpeed
    }

    /**
     * Returns the calorie burn of the run
     */
    @Exclude
    fun getCalories(): Int {
        return calories
    }

    /**
     * Returns the time it took to run 1km (in seconds)
     */
    @Exclude
    fun getTimeForOneKilometer(): Long {
        return timeForOneKilometer
    }

    /**
     * Returns the path of the run
     * DO NOT REMOVE, important for Firebase!
     */
    fun getPath(): Path {
        return path
    }
}
