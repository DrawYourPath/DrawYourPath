package com.github.drawyourpath.bootcamp.challenge

import java.time.LocalDate

data class DailyGoal(
    var distanceInKilometerGoal: Double,
    var timeInMinutesGoal: Double,
    var nbOfShapesGoal: Int,
    var distanceInKilometerProgress: Double = 0.0,
    var timeInMinutesProgress: Double = 0.0,
    var nbOfShapesProgress: Int = 0,
    val date: LocalDate = LocalDate.now()
) {

    /**
     * return the string corresponding to the position
     */
    fun getGoalToString(pos: Int): String {
        return when (pos) {
            0 -> "kilometers"
            1 -> "minutes"
            2 -> "shapes"
            else -> "Error: pos should be : -1 < pos < ${count()}"
        }
    }

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

    fun getGoalToDouble(pos: Int): Double {
        return when (pos) {
            0 -> distanceInKilometerGoal
            1 -> timeInMinutesGoal
            2 -> nbOfShapesGoal.toDouble()
            else -> 0.0
        }
    }

    fun getProgressToDouble(pos: Int): Double {
        return when (pos) {
            0 -> distanceInKilometerProgress
            1 -> timeInMinutesProgress
            2 -> nbOfShapesProgress.toDouble()
            else -> 0.0
        }
    }

    /**
     * return the number of attributes
     */
    fun count(): Int {
        return 3
    }

}
