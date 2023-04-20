package com.epfl.drawyourpath.userProfile.dailygoal

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epfl.drawyourpath.userProfile.cache.GoalAndProgress

@Dao
interface DailyGoalDao {

    /**
     * returns an ordered list read-only DailyGoal from most recent to least recent
     * @param userId the id of the user
     * @return [LiveData] of [DailyGoalEntity]
     */
    @Query("SELECT * FROM DailyGoal WHERE user_id = :userId ORDER BY date DESC")
    fun getDailyGoalById(userId: String): LiveData<List<DailyGoalEntity>>

    /**
     * TODO
     */
    @Query("SELECT * FROM DailyGoal WHERE user_id = :userId AND date = :date")
    fun getDailyGoalByIdAndDate(userId: String, date: Long): DailyGoalEntity

    /**
     * TODO
     */
    @Query("SELECT distance_goal, time_goal, paths_goal, total_distance, total_time, total_paths FROM User WHERE id = :userId")
    fun getGoalAndTotalProgress(userId: String): GoalAndProgress

    /**
     * TODO
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyGoal: DailyGoalEntity)

    /**
     * TODO
     */
    @Transaction
    fun updateProgress(userId: String, date: Long, distance: Double, time: Double, paths: Int) {
        val progress = getGoalAndTotalProgress(userId)
        updateTotalProgressUser(userId, progress.totalDistance + distance, progress.totalActivityTime + time, progress.totalNbOfPaths + paths)
        val dailyGoal = getDailyGoalByIdAndDate(userId, date)
        updateProgressDailyGoal(
            userId,
            date,
            dailyGoal.distanceInKilometerProgress + distance,
            dailyGoal.timeInMinutesProgress + time,
            dailyGoal.nbOfPathsProgress + paths
        )
    }

    /**
     * TODO
     */
    @Query("UPDATE DailyGoal SET distance_progress = :distance, time_progress = :time, path_progress = :paths WHERE user_id = :userId AND date = :date")
    fun updateProgressDailyGoal(userId: String, date: Long, distance: Double, time: Double, paths: Int)

    /**
     * TODO
     */
    @Query("UPDATE User SET total_distance = :distance, total_time = :time, total_paths = :paths WHERE id = :userId")
    fun updateTotalProgressUser(userId: String, distance: Double, time: Double, paths: Int)

    /**
     * set a new distance goal to DailyGoal and User with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param distanceGoal the new distance goal of the user
     */
    @Transaction
    fun updateDistanceGoal(userId: String, date: Long, distanceGoal: Double) {
        updateDistanceGoalUser(userId, distanceGoal)
        updateDistanceGoalDailyGoal(userId, date, distanceGoal)
    }

    /**
     * should not be used: use [updateDistanceGoal] instead
     * set a new distance goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE DailyGoal SET distance_goal = :distanceGoal WHERE user_id = :userId AND date = :date")
    fun updateDistanceGoalDailyGoal(userId: String, date: Long, distanceGoal: Double)

    /**
     * should not be used: use [updateDistanceGoal] instead
     * set a new distance goal to user with the corresponding id
     * @param id the id of the user
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE User SET distance_goal = :distanceGoal WHERE id = :id")
    fun updateDistanceGoalUser(id: String, distanceGoal: Double)

    /**
     * set a new time goal to DailyGoal and User with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param timeGoal the new time goal of the user
     */
    @Transaction
    fun updateTimeGoal(userId: String, date: Long, timeGoal: Double) {
        updateTimeGoalUser(userId, timeGoal)
        updateTimeGoalDailyGoal(userId, date, timeGoal)
    }

    /**
     * should not be used: use [updateTimeGoal] instead
     * set a new time goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE DailyGoal SET time_goal = :timeGoal WHERE user_id = :userId AND date = :date")
    fun updateTimeGoalDailyGoal(userId: String, date: Long, timeGoal: Double)

    /**
     * should not be used: use [updateTimeGoal] instead
     * set a new time goal to user with the corresponding id
     * @param id the id of the user
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE User SET time_goal = :timeGoal WHERE id = :id")
    fun updateTimeGoalUser(id: String, timeGoal: Double)

    /**
     * set a new number of paths goal to DailyGoal and User with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param pathsGoal the new number of paths goal of the user
     */
    @Transaction
    fun updatePathsGoal(userId: String, date: Long, pathsGoal: Int) {
        updatePathsGoalUser(userId, pathsGoal)
        updatePathsGoalDailyGoal(userId, date, pathsGoal)
    }

    /**
     * should not be used: use [updatePathsGoal] instead
     * set a new number of paths goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param pathsGoal the new number of paths goal of the user
     */
    @Query("UPDATE DailyGoal SET path_goal = :pathsGoal WHERE user_id = :userId AND date = :date")
    fun updatePathsGoalDailyGoal(userId: String, date: Long, pathsGoal: Int)

    /**
     * should not be used: use [updatePathsGoal] instead
     * set a new number of paths goal to user with the corresponding id
     * @param id the id of the user
     * @param pathsGoal the new number of paths goal of the user
     */
    @Query("UPDATE User SET paths_goal = :pathsGoal WHERE id = :id")
    fun updatePathsGoalUser(id: String, pathsGoal: Int)

    /**
     * delete the DailyGoal from the room database
     * @param dailyGoal the user to delete
     */
    @Delete
    fun delete(dailyGoal: DailyGoalEntity)

    /**
     * delete all the DailyGoals
     */
    @Query("DELETE FROM DailyGoal")
    fun clear()

}