package com.epfl.drawyourpath.userProfile.dailygoal

import java.time.LocalDate

/**
 * class representing the Daily Goal that the user set
 */
data class DailyGoal(
    var distanceInKilometerGoal: Double,
    var timeInMinutesGoal: Double,
    var nbOfPathsGoal: Int,
    var distanceInKilometerProgress: Double = 0.0,
    var timeInMinutesProgress: Double = 0.0,
    var nbOfPathsProgress: Int = 0,
    val date: LocalDate = LocalDate.now()
) : java.io.Serializable {

    /**
     * create a new Empty DailyGoal based on the current one
     *
     * @return a new DailyGoal
     */
    fun createNewGoalFromThis(): DailyGoal {
        return DailyGoal(this.distanceInKilometerGoal, this.timeInMinutesGoal, this.nbOfPathsGoal)
    }

    /**
     * constructor to transform a [DailyGoalEntity] into a [DailyGoal]
     * @param entity the entity to transform
     */
    constructor(entity: DailyGoalEntity) : this(
        entity.distanceInKilometerGoal,
        entity.timeInMinutesGoal,
        entity.nbOfPathsGoal,
        entity.distanceInKilometerProgress,
        entity.timeInMinutesProgress,
        entity.nbOfPathsProgress,
        entity.getDateAsLocalDate()
    )

    fun toDailyGoalEntity(userId: String): DailyGoalEntity {
        return DailyGoalEntity(
            userId,
            DailyGoalEntity.fromLocalDateToLong(date),
            distanceInKilometerGoal,
            timeInMinutesGoal,
            nbOfPathsGoal,
            distanceInKilometerProgress,
            timeInMinutesProgress,
            nbOfPathsProgress
        )
    }

}
