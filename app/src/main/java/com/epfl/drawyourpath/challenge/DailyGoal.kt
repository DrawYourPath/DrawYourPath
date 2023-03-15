package com.epfl.drawyourpath.challenge

import java.time.LocalDate

/**
 * class representing the Daily Goal that the user set
 */
data class DailyGoal(
    var distanceInKilometerGoal: Double,
    var timeInMinutesGoal: Double,
    var nbOfShapesGoal: Int,
    var distanceInKilometerProgress: Double = 0.0,
    var timeInMinutesProgress: Double = 0.0,
    var nbOfShapesProgress: Int = 0,
    val date: LocalDate = LocalDate.now()
) : java.io.Serializable {

    /**
     * create a new Empty DailyGoal based on the current one
     *
     * @return a new DailyGoal
     */
    fun createNewGoalFromThis(): DailyGoal {
        return DailyGoal(this.distanceInKilometerGoal, this.timeInMinutesGoal, this.nbOfShapesGoal)
    }

    /**
     * should only be used inside [DailyGoalViewAdapter]
     * get the unit associated with the goal
     * @param pos the position of the goal from 0 to [count] - 1
     *
     * @return the associated unit
     */
    fun getGoalUnit(pos: Int): String {
        return when (pos) {
            0 -> "kilometers"
            1 -> "minutes"
            2 -> "shapes"
            else -> "Error: pos should be : -1 < pos < ${count()}"
        }
    }

    /**
     * should only be used inside [DailyGoalViewAdapter]
     * update the goal associated to pos with a new value
     *
     * @param value the value
     * @param pos the position of the goal from 0 to [count] - 1
     */
    fun updateGoal(value: String, pos: Int) {
        val doubleValue = value.toDoubleOrNull()

        if (doubleValue == null || doubleValue <= 0) {
            return
        }
        when (pos) {
            0 -> distanceInKilometerGoal = doubleValue
            1 -> timeInMinutesGoal = doubleValue
            2 -> nbOfShapesGoal = doubleValue.toInt()
            else -> return
        }
    }

    /**
     * should only be used inside [DailyGoalViewAdapter]
     * get the value of the associated to the goal
     *
     * @param pos the position of the goal from 0 to [count] - 1
     *
     * @return the value of the goal
     */
    fun getGoalToDouble(pos: Int): Double {
        return when (pos) {
            0 -> distanceInKilometerGoal
            1 -> timeInMinutesGoal
            2 -> nbOfShapesGoal.toDouble()
            else -> 0.0
        }
    }

    /**
     * should only be used inside [DailyGoalViewAdapter]
     * get the value of the associated to the progress towards the goal
     *
     * @param pos the position of the goal from 0 to [count] - 1
     *
     * @return the value of the progress
     */
    fun getProgressToDouble(pos: Int): Double {
        return when (pos) {
            0 -> distanceInKilometerProgress
            1 -> timeInMinutesProgress
            2 -> nbOfShapesProgress.toDouble()
            else -> 0.0
        }
    }

    /**
     * should only be used inside [DailyGoalViewAdapter]
     * return the number of attributes
     */
    fun count(): Int {
        return 3
    }

}
