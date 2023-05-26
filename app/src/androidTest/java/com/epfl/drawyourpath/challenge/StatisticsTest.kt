package com.epfl.drawyourpath.challenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    // Total of each daily field is 30.
    private val goals = listOf(
        DailyGoal(
            10.0,
            10.0,
            10,
            10.0,
            10.0,
            10,
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            5.0,
            5.0,
            5,
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            0.0,
            0.0,
            0,
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            15.0,
            15.0,
            15,
        ),
    )
    private val goalsMonth = listOf(
        DailyGoal(
            10.0,
            10.0,
            10,
            10.0,
            10.0,
            10,
            LocalDate.now().minusMonths(2),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            5.0,
            5.0,
            5,
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            0.0,
            0.0,
            0,
            LocalDate.now(),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            0.0,
            0.0,
            0,
            LocalDate.now(),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            15.0,
            15.0,
            15,
            LocalDate.now(),
        ),
    )
    private val goalsYear = listOf(
        DailyGoal(
            10.0,
            10.0,
            10,
            10.0,
            10.0,
            10,
            LocalDate.now().minusYears(2),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            5.0,
            5.0,
            5,
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            0.0,
            0.0,
            0,
            LocalDate.now(),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            0.0,
            0.0,
            0,
            LocalDate.now(),
        ),
        DailyGoal(
            10.0,
            10.0,
            10,
            15.0,
            15.0,
            15,
            LocalDate.now(),
        ),
    )

    @Test
    fun totalDistanceForEmptyGoalsIsZero() {
        assertThat(Statistics.getTotalDistance(emptyList()), `is`(0.0))
    }

    @Test
    fun totalDistanceMatchesExpected() {
        assertThat(Statistics.getTotalDistance(goals), `is`(30.0))
    }

    @Test
    fun distancePerYearForEmptyGoalsIsZero() {
        val map = Statistics.getDistancePerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun distancePerYearMatchesExpected() {
        val map = Statistics.getDistancePerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(20.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }

    @Test
    fun totalTimeForEmptyGoalsIsZero() {
        assertThat(Statistics.getTotalTime(emptyList()), `is`(0.0))
    }

    @Test
    fun totalTimeMatchesExpected() {
        assertThat(Statistics.getTotalTime(goals), `is`(30.0))
    }

    @Test
    fun timePerYearForEmptyGoalsIsZero() {
        val map = Statistics.getTimePerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun timePerYearMatchesExpected() {
        val map = Statistics.getTimePerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(20.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }

    @Test
    fun reachedGoalsCountForEmptyGoalsIsZero() {
        assertThat(Statistics.getReachedGoalsCount(emptyList()), `is`(0))
    }

    @Test
    fun reachedGoalsMatchesExpected() {
        assertThat(Statistics.getReachedGoalsCount(goals), `is`(2))
    }

    @Test
    fun averageSpeedForEmptyGoalsIsZero() {
        assertThat(Statistics.getAverageSpeed(emptyList()), `is`(0.0))
    }

    @Test
    fun averageSpeedMatchesExpected() {
        assertThat(Statistics.getAverageSpeed(goals), `is`(1.0))
    }

    @Test
    fun averageSpeedPerMonthForEmptyGoalsIsZero() {
        val map = Statistics.getAverageSpeedPerMonth(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageSpeedPerMonthMatchesExpected() {
        val map = Statistics.getAverageSpeedPerMonth(goalsMonth)
        val day = LocalDate.now().dayOfMonth.toDouble()
        assertThat(map.getValue(day), `is`(1.0))
        assertThat(map.getValue(day % 31.0 + 1.0), `is`(0.0))
    }

    @Test
    fun averageSpeedPerYearForEmptyGoalsIsZero() {
        val map = Statistics.getAverageSpeedPerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageSpeedPerYearMatchesExpected() {
        val map = Statistics.getAverageSpeedPerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(1.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }

    @Test
    fun averageDurationForEmptyGoalsIsZero() {
        assertThat(Statistics.getAverageDuration(emptyList()), `is`(0.0))
    }

    @Test
    fun averageDurationMatchesExpected() {
        assertThat(Statistics.getAverageDuration(goals), `is`(7.5))
    }

    @Test
    fun averageDurationPerMonthForEmptyGoalsIsZero() {
        val map = Statistics.getAverageDurationPerMonth(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageDurationPerMonthMatchesExpected() {
        val map = Statistics.getAverageDurationPerMonth(goalsMonth)
        val day = LocalDate.now().dayOfMonth.toDouble()
        assertThat(map.getValue(day), `is`(5.0))
        assertThat(map.getValue(day % 31.0 + 1.0), `is`(0.0))
    }

    @Test
    fun averageDurationPerYearForEmptyGoalsIsZero() {
        val map = Statistics.getAverageDurationPerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageDurationPerYearMatchesExpected() {
        val map = Statistics.getAverageDurationPerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(5.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }

    @Test
    fun averageDistanceForEmptyGoalsIsZero() {
        assertThat(Statistics.getAverageDistance(emptyList()), `is`(0.0))
    }

    @Test
    fun averageDistanceMatchesExpected() {
        assertThat(Statistics.getAverageDistance(goals), `is`(7.5))
    }

    @Test
    fun averageDistancePerMonthForEmptyGoalsIsZero() {
        val map = Statistics.getAverageDistancePerMonth(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageDistancePerMonthMatchesExpected() {
        val map = Statistics.getAverageDistancePerMonth(goalsMonth)
        val day = LocalDate.now().dayOfMonth.toDouble()
        assertThat(map.getValue(day), `is`(5.0))
        assertThat(map.getValue(day % 31.0 + 1.0), `is`(0.0))
    }

    @Test
    fun averageDistancePerYearForEmptyGoalsIsZero() {
        val map = Statistics.getAverageDistancePerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun averageDistancePerYearMatchesExpected() {
        val map = Statistics.getAverageDistancePerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(5.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }

    @Test
    fun shapeDrawnCountForEmptyGoalsIsZero() {
        assertThat(Statistics.getShapeDrawnCount(emptyList()), `is`(0))
    }

    @Test
    fun shapeDrawnCountMatchesExpected() {
        assertThat(Statistics.getShapeDrawnCount(goals), `is`(30))
    }

    @Test
    fun shapeDrawnCountPerYearForEmptyGoalsIsZero() {
        val map = Statistics.getShapeDrawnCountPerYear(emptyList())
        assertThat(map.values.all { it == 0.0 }, `is`(true))
        assertThat(map.getValue(1.0), `is`(0.0))
    }

    @Test
    fun shapeDrawnCountPerYearMatchesExpected() {
        val map = Statistics.getShapeDrawnCountPerYear(goalsYear)
        val month = LocalDate.now().monthValue.toDouble()
        assertThat(map.getValue(month), `is`(20.0))
        assertThat(map.getValue(month % 11.0 + 1.0), `is`(0.0))
    }
}
