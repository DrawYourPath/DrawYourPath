package com.epfl.drawyourpath.challenge.dailygoal

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epfl.drawyourpath.challenge.milestone.Milestone
import com.epfl.drawyourpath.challenge.milestone.MilestoneEntity
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.path.cache.PointsEntity
import com.epfl.drawyourpath.path.cache.RunEntity
import com.epfl.drawyourpath.userProfile.cache.GoalAndAchievements

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
     * returns the daily goals of the user
     * @param userId the id of the user
     * @return list of [DailyGoalEntity]
     */
    @Query("SELECT * FROM DailyGoal WHERE user_id = :userId")
    fun getAllDailyGoalOfUser(userId: String): List<DailyGoalEntity>

    /**
     * returns the dailyGoal associated to the user and date
     * @param userId the user id
     * @param date the date
     * @return the dailyGoal
     */
    @Query("SELECT * FROM DailyGoal WHERE user_id = :userId AND date = :date")
    fun getDailyGoalByIdAndDate(userId: String, date: Long): DailyGoalEntity

    /**
     * returns the [GoalAndAchievements] of the user
     * @param userId the user id
     * @return the goal and total progress of the user
     */
    @Query("SELECT distance_goal, time_goal, paths_goal, total_distance, total_time, total_paths FROM User WHERE id = :userId")
    fun getGoalAndTotalProgress(userId: String): GoalAndAchievements

    /**
     * returns the milestones of the user
     * @param userId the user id
     * @return [LiveData] of a list of [MilestoneEntity]
     */
    @Query("SELECT * FROM Milestone WHERE user_id = :userId")
    fun getMilestonesById(userId: String): LiveData<List<MilestoneEntity>>

    /**
     * returns the milestones of the user
     * @param userId the user id
     * @return a list of [MilestoneEntity]
     */
    @Query("SELECT * FROM Milestone WHERE user_id = :userId")
    fun getMilestones(userId: String): List<MilestoneEntity>

    /**
     * insert a new dailyGoal inside the room database and will replace if there is a conflict with the id and date
     * @param dailyGoal the dailyGoal to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyGoal(dailyGoal: DailyGoalEntity)

    /**
     * add the run and update the progress of the dailyGoal and total progress of the User
     * @param userId the user id
     * @param date the date of the progress
     * @param progress the progress to add
     * @param run the run to add
     * @param points the path of the run
     * @return the new daily goal and the milestones
     */
    @Transaction
    fun addRunAndUpdateProgress(userId: String, date: Long, progress: UserGoals, run: RunEntity, points: List<PointsEntity>): Pair<DailyGoalEntity, List<MilestoneEntity>> {
        addTotalProgressUser(userId, progress.distance ?: 0.0, progress.activityTime ?: 0.0, progress.paths?.toInt() ?: 0)
        insertIfDailyGoalUpdateFailed(userId, date, addProgressDailyGoal(userId, date, progress.distance ?: 0.0, progress.activityTime ?: 0.0, progress.paths?.toInt() ?: 0), progress)
        insertRun(run)
        insertAllPoints(points)
        insertMilestones(getMilestonesToAdd(userId, getAllDailyGoalOfUser(userId)))
        return Pair(getDailyGoalByIdAndDate(userId, date), getMilestones(userId))
    }

    /**
     * should not be used: use [addRunAndUpdateProgress] instead
     * add the progress of the daily goal
     * @param userId the user id
     * @param date the date
     * @param distance the distance to set
     * @param time the time to set
     * @param paths the number of paths to set
     */
    @Query("UPDATE DailyGoal SET distance_progress = distance_progress + :distance, time_progress = time_progress + :time, path_progress = path_progress + :paths WHERE user_id = :userId AND date = :date")
    fun addProgressDailyGoal(userId: String, date: Long, distance: Double, time: Double, paths: Int): Int

    /**
     * should not be used: use [addRunAndUpdateProgress] instead
     * add the total progress of the user
     * @param userId the user id
     * @param distance the distance to set
     * @param time the time to set
     * @param paths the number of paths to set
     */
    @Query("UPDATE User SET total_distance = total_distance + :distance, total_time = total_time + :time, total_paths = total_paths + :paths WHERE id = :userId")
    fun addTotalProgressUser(userId: String, distance: Double, time: Double, paths: Int)

    /**
     * should not be used: use [addRunAndUpdateProgress] instead
     * insert the milestones in the cache only if they are new
     * @param milestones the milestones
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMilestones(milestones: List<MilestoneEntity>)

    /**
     * should not be used: use [addRunAndUpdateProgress] instead
     * insert the run inside the cache
     * @param run the run
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: RunEntity)

    /**
     * should not be used: use [addRunAndUpdateProgress] instead
     * insert the points inside the cache
     * @param points the points
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPoints(points: List<PointsEntity>)

    /**
     * set a new distance, time, path goal to DailyGoal and User with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param goals the new goals of the user
     * @return the new daily goal
     */
    @Transaction
    fun updateGoals(userId: String, date: Long, goals: UserGoals): DailyGoalEntity {
        goals.distance?.apply {
            updateDistanceGoalUser(userId, this)
            insertIfDailyGoalUpdateFailed(userId, date, updateDistanceGoalDailyGoal(userId, date, this))
        }
        goals.activityTime?.apply {
            updateTimeGoalUser(userId, this)
            insertIfDailyGoalUpdateFailed(userId, date, updateTimeGoalDailyGoal(userId, date, this))
        }
        goals.paths?.toInt()?.apply {
            updatePathsGoalUser(userId, this)
            insertIfDailyGoalUpdateFailed(userId, date, updatePathsGoalDailyGoal(userId, date, this))
        }
        return getDailyGoalByIdAndDate(userId, date)
    }

    /**
     * should not be used: use [updateGoals] instead
     * set a new distance goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE DailyGoal SET distance_goal = :distanceGoal WHERE user_id = :userId AND date = :date")
    fun updateDistanceGoalDailyGoal(userId: String, date: Long, distanceGoal: Double): Int

    /**
     * should not be used: use [updateGoals] instead
     * set a new distance goal to user with the corresponding id
     * @param id the id of the user
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE User SET distance_goal = :distanceGoal WHERE id = :id")
    fun updateDistanceGoalUser(id: String, distanceGoal: Double)

    /**
     * should not be used: use [updateGoals] instead
     * set a new time goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE DailyGoal SET time_goal = :timeGoal WHERE user_id = :userId AND date = :date")
    fun updateTimeGoalDailyGoal(userId: String, date: Long, timeGoal: Double): Int

    /**
     * should not be used: use [updateGoals] instead
     * set a new time goal to user with the corresponding id
     * @param id the id of the user
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE User SET time_goal = :timeGoal WHERE id = :id")
    fun updateTimeGoalUser(id: String, timeGoal: Double)

    /**
     * should not be used: use [updateGoals] instead
     * set a new number of paths goal to DailyGoal with the corresponding id and date
     * @param userId the id of the user
     * @param date the date of the daily goal
     * @param pathsGoal the new number of paths goal of the user
     */
    @Query("UPDATE DailyGoal SET path_goal = :pathsGoal WHERE user_id = :userId AND date = :date")
    fun updatePathsGoalDailyGoal(userId: String, date: Long, pathsGoal: Int): Int

    /**
     * should not be used: use [updateGoals] instead
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

    /**
     * get the milestones that are reached
     * @param userId the user id
     * @param dailyGoals the daily goals
     * @return the milestones reached
     */
    private fun getMilestonesToAdd(userId: String, dailyGoals: List<DailyGoalEntity>): List<MilestoneEntity> {
        return Milestone.getAllReachedMilestones(dailyGoals.map { DailyGoal(it) }).map { MilestoneEntity(userId, it) }
    }

    /**
     * create a new DailyGoal if the update was unsuccessful (no DailyGoal at the specified day)
     * @param userId the user id
     * @param date the date
     * @param updated the number of rows updated
     * @param progress the progress to add
     */
    private fun insertIfDailyGoalUpdateFailed(userId: String, date: Long, updated: Int, progress: UserGoals = UserGoals()) {
        if (updated == 0) {
            val goalAndProgress = getGoalAndTotalProgress(userId)
            insertDailyGoal(
                DailyGoalEntity(
                    userId,
                    date,
                    goalAndProgress.distanceGoal,
                    goalAndProgress.activityTimeGoal,
                    goalAndProgress.nbOfPathsGoal,
                    progress.distance ?: 0.0,
                    progress.activityTime ?: 0.0,
                    progress.paths?.toInt() ?: 0,
                ),
            )
        }
    }
}
