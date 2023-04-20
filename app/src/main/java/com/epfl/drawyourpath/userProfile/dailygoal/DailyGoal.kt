package com.epfl.drawyourpath.userProfile.dailygoal

import java.time.LocalDate

/**
 * class representing the Daily Goal that the user set
 */
data class DailyGoal(
    var distanceInKilometerGoal: Double,
    var activityTimeInMinutesGoal: Double,
    var nbOfPathsGoal: Int,
    var distanceInKilometerProgress: Double = 0.0,
    var activityTimeInMinutesProgress: Double = 0.0,
    var nbOfPathsProgress: Int = 0,
    val date: LocalDate = LocalDate.now()
) {

    /**
     * create a new Empty DailyGoal based on the current one
     *
     * @return a new DailyGoal
     */
    fun createNewGoalFromThis(): DailyGoal {
        return DailyGoal(this.distanceInKilometerGoal, this.activityTimeInMinutesGoal, this.nbOfPathsGoal)
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
        entity.getDateAsLocalDate()
    )

    fun toDailyGoalEntity(userId: String): DailyGoalEntity {
        return DailyGoalEntity(
            userId,
            DailyGoalEntity.fromLocalDateToLong(date),
            distanceInKilometerGoal,
            activityTimeInMinutesGoal,
            nbOfPathsGoal,
            distanceInKilometerProgress,
            activityTimeInMinutesProgress,
            nbOfPathsProgress
        )
    }

    companion object {
        val TEST_SAMPLE = DailyGoal(23.0, 86.0, 2, 17.6543, 39.01247, 1)
    }

}
