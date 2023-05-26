package com.epfl.drawyourpath.challenge

import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import java.time.LocalDate

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
        return dailyGoals.fold(0.0) { acc, dailyGoal ->
            acc + dailyGoal.distance
        }
    }

    /**
     * Computes the distance of the specified goals per month in the current year.
     * @param dailyGoals The list of goals we want to compute the distance.
     * @return A map from the month to the distance of the goals in the current year.
     */
    fun getDistancePerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getTotalDistance(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the total time of the specified goals.
     * @param dailyGoals The list of goals we want to compute the time.
     * @return The total time of the goals.
     */
    fun getTotalTime(dailyGoals: List<DailyGoal>): Double {
        return dailyGoals.fold(0.0) { acc, dailyGoal ->
            acc + dailyGoal.time
        }
    }

    /**
     * Computes the time of the specified goals per month in the current year.
     * @param dailyGoals The list of goals we want to compute the time.
     * @return A map from the month to the time of the goals in the current year.
     */
    fun getTimePerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getTotalTime(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the number of goals that were reached.
     * @param dailyGoals The target goals.
     * @return The total number of reached goals.
     */
    fun getReachedGoalsCount(dailyGoals: List<DailyGoal>): Int {
        return dailyGoals.fold(0) { acc, dailyGoal ->
            acc + if (dailyGoal.wasReached()) 1 else 0
        }
    }

    /**
     * Computes the average speed of the specified goals.
     * @param dailyGoals The list of goals we want to compute the average speed.
     * @return The average speed for the goals.
     */
    fun getAverageSpeed(dailyGoals: List<DailyGoal>): Double {
        val totalTime = getTotalTime(dailyGoals)
        return if (totalTime == 0.0) 0.0 else (getTotalDistance(dailyGoals) / totalTime)
    }

    /**
     * Computes the average speed per day in the current month.
     * @param dailyGoals The list of goals we want to compute the average speed.
     * @return A map from the day to the average speed in the current month.
     */
    fun getAverageSpeedPerMonth(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().dayOfMonth).map { it.toDouble() }.associateWith { day ->
            getAverageSpeed(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == LocalDate.now().monthValue && it.date.dayOfMonth == day.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the average speed per month in the current year.
     * @param dailyGoals The list of goals we want to compute the average speed.
     * @return A map from the month to the average speed in the current year.
     */
    fun getAverageSpeedPerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getAverageSpeed(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the average duration of the specified goals.
     * @param dailyGoals The list of goals we want to compute the average duration.
     * @return The average duration for the goals.
     */
    fun getAverageDuration(dailyGoals: List<DailyGoal>): Double {
        return if (dailyGoals.isEmpty()) 0.0 else (getTotalTime(dailyGoals) / dailyGoals.size)
    }

    /**
     * Computes the average duration per day in the current month.
     * @param dailyGoals The list of goals we want to compute the average duration.
     * @return A map from the day to the average duration in the current month.
     */
    fun getAverageDurationPerMonth(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().dayOfMonth).map { it.toDouble() }.associateWith { day ->
            getAverageDuration(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == LocalDate.now().monthValue && it.date.dayOfMonth == day.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the average duration per month in the current year.
     * @param dailyGoals The list of goals we want to compute the average duration.
     * @return A map from the month to the average duration in the current year.
     */
    fun getAverageDurationPerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getAverageDuration(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the average distance of the specified goals.
     * @param dailyGoals The list of goals we want to compute the average distance.
     * @return The average distance for the goals.
     */
    fun getAverageDistance(dailyGoals: List<DailyGoal>): Double {
        return if (dailyGoals.isEmpty()) 0.0 else (getTotalDistance(dailyGoals) / dailyGoals.size)
    }

    /**
     * Computes the average distance per day in the current month.
     * @param dailyGoals The list of goals we want to compute the average distance.
     * @return A map from the day to the average distance in the current month.
     */
    fun getAverageDistancePerMonth(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().dayOfMonth).map { it.toDouble() }.associateWith { day ->
            getAverageDistance(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == LocalDate.now().monthValue && it.date.dayOfMonth == day.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the average distance per month in the current year.
     * @param dailyGoals The list of goals we want to compute the average distance.
     * @return A map from the month to the average distance in the current year.
     */
    fun getAverageDistancePerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getAverageDistance(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            )
        }.withDefault { 0.0 }
    }

    /**
     * Computes the total number of shapes drawn for the specified goals.
     * @param dailyGoals The list of goals we want to compute the number of shapes drawn.
     * @return The total number of shapes drawn.
     */
    fun getShapeDrawnCount(dailyGoals: List<DailyGoal>): Int {
        return dailyGoals.fold(0) { acc, dailyGoal ->
            acc + dailyGoal.paths
        }
    }

    /**
     * Computes the number of shapes drawn for goals per month in the current year.
     * @param dailyGoals The list of goals we want to compute the number of shapes drawn.
     * @return A map from the month to the number of shapes drawn for goals in the current year.
     */
    fun getShapeDrawnCountPerYear(dailyGoals: List<DailyGoal>): Map<Double, Double> {
        return (1..LocalDate.now().monthValue).map { it.toDouble() }.associateWith { month ->
            getShapeDrawnCount(
                dailyGoals.filter {
                    it.date.year == LocalDate.now().year && it.date.monthValue == month.toInt()
                }
            ).toDouble()
        }.withDefault { 0.0 }
    }
}
