package com.github.drawyourpath.bootcamp.path

import com.firebase.ui.auth.data.model.User


/**
 * A class representing a run taken by a user.
 * The run includes information such as the path taken, distance, duration, timestamp,
 * user, averageSpeed, and calorie burn.
 * Various methods are included to calculate and retrieve this information.
 */
class Run (
    val user: User,
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
        val durationInSeconds = (endTime - startTime) / 1000.0
        averageSpeed = distance / durationInSeconds

    }

    private fun calculateDuration() {
        duration = endTime - startTime
    }


    fun calculateCalorieBurn() {
        calories = 0
        //TODO implement later based on the characteristics of the user
    }

    private fun calculateDistance() {
        this.distance = path.getDistance()
    }

    fun getDistance(): Double {
        return distance
    }

    fun getDuration(): Long {
        return duration
    }

    fun getStartTime(): Long {
        return startTime
    }

    fun getEndTime(): Long {
        return endTime
    }

    fun getAverageSpeed(): Double {
        return averageSpeed
    }

    fun getCalories(): Int {
        return calories
    }




}