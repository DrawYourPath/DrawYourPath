package com.epfl.drawyourpath.path

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow

/**
 * A class representing a run taken by a user.
 * The run includes information such as the path taken, distance, duration, timestamp,
 * user, averageSpeed, and calorie burn.
 * Various methods are included to calculate and retrieve this information.
 */
class Run(
    private val path: Path, // represents the path taken by the user
    private val startTime: Long, // the timestamps of the run
    private var duration: Long, //represent the duration of the run(in seconds)
    private val endTime: Long,
) {

    init {
        if (endTime < startTime) {
            throw IllegalArgumentException("End time must be greater than start time")
        }
        calculateDistance()
        calculateAverageSpeed()
        calculateTimeForOneKilometer()
        calculateCalorieBurn()
    }

    // the distance of the run (in meters)
    private var distance: Double = 0.0

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

    /**
     * Calculates the calorie burn of the run based on the characteristics of the run.
     */
    private fun calculateCalorieBurn() {
        // We can compute the calories by using the MET (metabolic equivalent of task)
        // The MET is the ratio of the work metabolic rate to the resting metabolic rate
        // The resting metabolic rate is 1.0*kcal/(hours*kg)

        // Sources :
        //    Ainsworth, B. E., Haskell, W. L., Herrmann, S. D., Meckes, N.,
        //    Bassett Jr, D. R., Tudor-Locke, C., ... & Leon, A. S. (2011).
        //    2011 Compendium of Physical Activities: a second update of codes and MET values.
        //    Medicine & science in sports & exercise, 43(8), 1575-1581.
        // Links :
        //    https://www.codinma.es/wp-content/uploads/2018/11/Second-compendium-METS-2011.pdf
        //    https://cdn-links.lww.com/permalink/mss/a/mss_43_8_2011_06_13_ainsworth_202093_sdc1.pdf

        // The MET is calculated with a 4th degree polynomial approximation of the
        // MET values from the 2011 Compendium of Physical Activities
        val met = 0.05506 * averageSpeed.pow(4) - 0.6525 * averageSpeed.pow(3) +
            2.506 * averageSpeed.pow(2) - 0.1385 * averageSpeed.pow(1) + 1.486
        val averageWeight = 70
        val cal = met * averageWeight * duration / 3600
        calories = cal.toInt()
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

    /**
     * This function returns the distance of each sections of the path(in order of the drawing)
     */
    fun getSectionsDistance(): List<Double> {
        val list = mutableListOf<Double>()
        this.path.getPoints().forEachIndexed{index, _ ->
            list.add(this.path.getDistanceInSection(index))
        }
        return list
    }

    /**
     * This function returns the time taken to draw each section of the path(in order of the drawing)
     * We consider that each seconds a points is added to a section.
     */
    fun getSectionsDuration(): List<Long> {
        val list = mutableListOf<Long>()
        val step = this.path.size()/duration
        this.path.getPoints().forEachIndexed{index, _ ->
            list.add(this.path.sizeOfSection(index).toLong() * step)
        }
        return list
    }

    /**
     * This function returns the average speed taken to draw each section of the path(in order of the drawing) in m/s
     * We consider that each seconds a points is added to a section.
     */
    fun getSectionsAvgSpeed(): List<Double> {
        val list = mutableListOf<Double>()
        val distance = getSectionsDistance()
        val time = getSectionsDuration()
        distance.forEachIndexed{index, _ ->
            list.add(distance[index]/time[index])
        }
        return list
    }

    /**
     * Function used to get the time taken by the user to throw each kilometer
     */
    fun getKilometersDuration(): List<Long>{
        val step: Int = (path.size()/duration).toInt()
        val listTime = mutableListOf<Long>()
        val newPath = Path()
        var totalTime = 0L
        for(section in this.path.getPoints()){
            for(point in section){
                newPath.addPointToLastSection(point)
                if(newPath.getDistance()>=((listTime.size+1)*1000)){
                    val timeTaken = step * (newPath.size().toLong()) - totalTime
                    listTime.add(timeTaken)
                    totalTime += timeTaken
                }
            }
            newPath.addNewSection()
        }
        return listTime
    }

    /**
     * Function used to get the average speed taken by the user to throw each kilometer
     */
    fun getKilometersAvgSpeed(): List<Double>{
        return getKilometersDuration().map { t -> 1000.0/t }
    }
}
