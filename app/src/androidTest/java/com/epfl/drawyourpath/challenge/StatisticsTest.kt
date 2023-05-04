package com.epfl.drawyourpath.challenge

import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    // Total of each daily field is 30.
    val goals = listOf(
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

    @Test
    fun totalDistanceForEmptyGoalsIsZero() {
        assertThat(Statistics.getTotalDistance(emptyList()), `is`(0.0))
    }

    @Test
    fun totalDistanceMatchesExpected() {
        assertThat(Statistics.getTotalDistance(goals), `is`(30.0))
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
    fun shapeDrawnCountForEmptyGoalsIsZero() {
        assertThat(Statistics.getShapeDrawnCount(emptyList()), `is`(0))
    }

    @Test
    fun shapeDrawnCountMatchesExpected() {
        assertThat(Statistics.getShapeDrawnCount(goals), `is`(30))
    }
}
