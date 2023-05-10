package com.epfl.drawyourpath.challenge

import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal

/**
 * Helper object to compute all kind of statistics.
 */
object Statistics {

    /**
     * Computes the total distance of the specified goals.
     * @param dailyGoals The list of goals we want to compute the distance.
     * @return The total distance of the goals.
     */
    fun getTotalDistance(dailyGoals: List<DailyGoal>): Double {
        return dailyGoals.fold(0.0) {
                acc, dailyGoal ->
            acc + dailyGoal.distance
        }
    }

    /**
     * Computes the number of goals that were reached.
     * @param dailyGoals The target goals.
     * @return The total number of reached goals.
     */
    fun getReachedGoalsCount(dailyGoals: List<DailyGoal>): Int {
        return dailyGoals.fold(0) {
                acc, dailyGoal ->
            acc + if (dailyGoal.wasReached()) 1 else 0
        }
    }

    /**
     * Computes the average speed of the specified goals.
     * @param dailyGoals The list of goals we want to compute the average speed.
     * @return The average speed for the goals.
     */
    fun getAverageSpeed(dailyGoals: List<DailyGoal>): Double {
        return dailyGoals.fold(Pair(0.0, 0.0)) {
                acc, dailyGoal ->
            Pair(acc.first + dailyGoal.distance, acc.second + dailyGoal.time)
        }.let { if (it.second == 0.0) 0.0 else it.first / it.second }
    }

    /**
     * Computes the total number of shapes drawn for the specified goals.
     * @param dailyGoals The list of goals we want to compute the distance.
     * @return The total number of shapes drawn.
     */
    fun getShapeDrawnCount(dailyGoals: List<DailyGoal>): Int {
        return dailyGoals.fold(0) {
                acc, dailyGoal ->
            acc + dailyGoal.paths
        }
    }
}
