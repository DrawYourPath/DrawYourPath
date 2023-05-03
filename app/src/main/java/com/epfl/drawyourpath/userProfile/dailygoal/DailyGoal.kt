package com.epfl.drawyourpath.userProfile.dailygoal

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

    /**
     * create a new Empty DailyGoal based on the current one
     *
     * @return a new DailyGoal
     */
    fun createNewGoalFromThis(): DailyGoal {
        return DailyGoal(this.expectedDistance, this.expectedTime, this.expectedPaths)
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
        entity.getDateAsLocalDate(),
    )

    fun toDailyGoalEntity(userId: String): DailyGoalEntity {
        return DailyGoalEntity(
            userId,
            DailyGoalEntity.fromLocalDateToLong(date),
            expectedDistance,
            expectedTime,
            expectedPaths,
            distance,
            time,
            paths,
        )
    }

    companion object {
        val TEST_SAMPLE = DailyGoal(23.0, 86.0, 2, 17.6543, 39.01247, 1)
    }
}
