package com.epfl.drawyourpath.userProfile.dailygoal

import com.epfl.drawyourpath.userProfile.UserProfile
import java.time.LocalDate

/**
 * class representing the Daily Goal that the user set
 */
data class DailyGoal(
    var expectedDistance: Double,
    var expectedTime: Double,
    var expectedPaths: Int,
    var distance: Double = 0.0,
    var time: Double = 0.0,
    var paths: Int = 0,
    val date: LocalDate = LocalDate.now(),
) {

    constructor(goals: UserProfile.Goals) : this(
        goals.distanceGoal,
        goals.activityTimeGoal,
        goals.pathsGoal,
    )

    /**
     * Checks if the user reached the goal this day.
     * @return True if all goals were reached.
     */
    fun wasReached(): Boolean {
        return distance >= expectedDistance &&
            time >= expectedTime &&
            paths >= expectedPaths
    }

    /**
     * constructor to transform a [DailyGoalEntity] into a [DailyGoal]
     * @param entity the entity to transform
     */
    constructor(entity: DailyGoalEntity) : this(
        entity.distanceInKilometerGoal,
        entity.activityTimeInMinutesGoal,
        entity.nbOfPathsGoal,
        entity.distanceInKilometerProgress,
        entity.activityTimeInMinutesProgress,
        entity.nbOfPathsProgress,
        LocalDate.ofEpochDay(entity.date),
    )

    companion object {
        val TEST_SAMPLE = DailyGoal(23.0, 86.0, 2, 17.6543, 39.01247, 1)
    }
}
