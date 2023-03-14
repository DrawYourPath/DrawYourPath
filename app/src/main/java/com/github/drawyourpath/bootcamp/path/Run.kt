package com.github.drawyourpath.bootcamp.path

import com.firebase.ui.auth.data.model.User


/**
 * A class representing a run taken by a user.
 * The run includes information such as the path taken, distance, duration, timestamp,
 * user, averageSpeed, and calorie burn.
 * Various methods are included to calculate and retrieve this information.
 */
class Run (
    //val user: User,             //TODO add later
    private val path: Path,       //represents the path taken by the user
    private val startTime: Long,  //the timestamps of the run
    private val endTime: Long
        ){


    init {
        if (endTime <= startTime) {
            throw IllegalArgumentException("End time must be greater than start time")
        }
        calculateDistance()
        calculateDuration()
        calculateAverageSpeed()
    }



    //the distance of the run (in meters)
    private var distance: Double = 0.0
    //the duration of the run (in seconds)
    private var duration: Long = 0L
    //the average speed of the run (in meters per second)
    private var averageSpeed: Double = 0.0
    //the calorie burn of the run
    private var calories: Int = 0



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
        calories = 0
        //TODO implement later based on the characteristics of the user
    }

    private fun calculateDistance() {
        this.distance = path.getDistance()
    }

    /**
     * Returns the distance of the run (in meters)
     */
    fun getDistance(): Double {
        return distance
    }


    /**
     * Returns the duration of the run (in seconds)
     */
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
     * Returns the end time of the run (in seconds)
     */
    fun getEndTime(): Long {
        return endTime
    }

    /**
     * Returns the average speed of the run (in meters per second)
     */
    fun getAverageSpeed(): Double {
        return averageSpeed
    }

    /**
     * Returns the calorie burn of the run
     */
    fun getCalories(): Int {
        return calories
    }




}