package com.epfl.drawyourpath.userProfile.dailygoal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.userProfile.cache.UserEntity
import java.time.LocalDate

@Entity(
    tableName = "DailyGoal",
    primaryKeys = ["userId, date"],
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE)]
)
data class DailyGoalEntity(

    @ColumnInfo(name = "user_id")
    val userId: String,

    val date: Long,

    @ColumnInfo(name = "distance_goal")
    val distanceInKilometerGoal: Double,

    @ColumnInfo(name = "time_goal")
    val timeInMinutesGoal: Double,

    @ColumnInfo(name = "path_goal")
    val nbOfPathsGoal: Int,

    @ColumnInfo(name = "distance_progress")
    val distanceInKilometerProgress: Double = 0.0,

    @ColumnInfo(name = "time_progress")
    val timeInMinutesProgress: Double = 0.0,

    @ColumnInfo(name = "path_progress")
    val nbOfPathsProgress: Int = 0,

    ) {

    /**
     * get the date of the DailyGoal
     * @return the date of the DailyGoal
     */
    fun getDateAsLocalDate(): LocalDate {
        return LocalDate.ofEpochDay(date)
    }

    companion object {
        /**
         * create a Long from a LocalDate
         * @param date the localDate
         * @return the Long
         */
        fun fromLocalDateToLong(date: LocalDate): Long {
            return date.toEpochDay()
        }
    }

}
