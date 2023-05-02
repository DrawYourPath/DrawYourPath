package com.epfl.drawyourpath.userProfile.dailygoal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.userProfile.cache.UserEntity
import java.time.LocalDate

@Entity(
    tableName = "DailyGoal",
    primaryKeys = ["user_id", "date"],
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE)],
)
data class DailyGoalEntity(

    @ColumnInfo(name = "user_id")
    val userId: String,

    val date: Long,

    @ColumnInfo(name = "distance_goal")
    val distanceInKilometerGoal: Double,

    @ColumnInfo(name = "time_goal")
    val activityTimeInMinutesGoal: Double,

    @ColumnInfo(name = "path_goal")
    val nbOfPathsGoal: Int,

    @ColumnInfo(name = "distance_progress")
    val distanceInKilometerProgress: Double = 0.0,

    @ColumnInfo(name = "time_progress")
    val activityTimeInMinutesProgress: Double = 0.0,

    @ColumnInfo(name = "path_progress")
    val nbOfPathsProgress: Int = 0,

) {
    constructor(dailyGoal: DailyGoal, userId: String) : this(
        userId,
        dailyGoal.date.toEpochDay(),
        dailyGoal.expectedDistance,
        dailyGoal.expectedTime,
        dailyGoal.expectedPaths,
        dailyGoal.distance,
        dailyGoal.time,
        dailyGoal.paths,
    )
}
