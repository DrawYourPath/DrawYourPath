package com.epfl.drawyourpath.userProfile.cache

import androidx.room.ColumnInfo
import com.epfl.drawyourpath.userProfile.UserModel

data class GoalAndAchievements(
    /**
     * the distance goal of the user
     */
    @ColumnInfo(name = "distance_goal")
    val distanceGoal: Double = 0.0,

    /**
     * the activity time goal of the user
     */
    @ColumnInfo(name = "time_goal")
    val activityTimeGoal: Double = 0.0,

    /**
     * the number of paths goal of the user
     */
    @ColumnInfo(name = "paths_goal")
    val nbOfPathsGoal: Int = 0,

    /**
     * the total distance of the user
     */
    @ColumnInfo(name = "total_distance")
    val totalDistance: Double = 0.0,

    /**
     * the total activity time of the user
     */
    @ColumnInfo(name = "total_time")
    val totalActivityTime: Double = 0.0,

    /**
     * the total number of paths of the user
     */
    @ColumnInfo(name = "total_paths")
    val totalNbOfPaths: Int = 0,
) {
    /**
     * construct a [GoalAndAchievements] from a [UserModel]
     */
    constructor(userModel: UserModel) : this(
        userModel.getCurrentDistanceGoal(),
        userModel.getCurrentActivityTime(),
        userModel.getCurrentNumberOfPathsGoal(),
        userModel.getTotalDistance(),
        userModel.getTotalActivityTime(),
        userModel.getTotalNbOfPaths(),
    )
}
