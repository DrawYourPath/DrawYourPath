package com.epfl.drawyourpath.userProfile.dailygoal

import androidx.lifecycle.LiveData
import androidx.room.*

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyGoal: DailyGoalEntity)

    //TODO
    //fun addProgress()

    /**
     * set a new distance goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE DailyGoal SET distance_goal = :distanceGoal WHERE user_id = :userId AND date = :date")
    fun updateDistanceGoal(userId: String, date: Long, distanceGoal: Double)

    /**
     * set a new time goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE DailyGoal SET distance_goal = :timeGoal WHERE user_id = :userId AND date = :date")
    fun updateTimeGoal(userId: String, date: Long, timeGoal: Double)

    /**
     * set a new number of paths goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param pathsGoal the new number of paths goal of the user
     */
    @Query("UPDATE DailyGoal SET distance_goal = :pathsGoal WHERE user_id = :userId AND date = :date")
    fun updatePathsGoal(userId: String, date: Long, pathsGoal: Int)

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