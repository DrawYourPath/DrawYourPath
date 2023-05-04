package com.epfl.drawyourpath.userProfile.cache

import androidx.room.ColumnInfo
import com.epfl.drawyourpath.database.UserData
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.userProfile.UserProfile

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
     * construct a [GoalAndAchievements] from a [UserData]
     * TODO add total when in userData
     */
    constructor(userGoals: UserGoals) : this(
        userGoals.distance ?: 0.0,
        userGoals.activityTime?.toDouble() ?: 0.0,
        userGoals.paths?.toInt() ?: 0,
        0.0,
        0.0,
        0,
    )

    constructor(goals: UserProfile.Goals) : this(
        goals.distanceGoal,
        goals.activityTimeGoal,
        goals.pathsGoal,
        0.0,
        0.0,
        0,
    )
}
