package com.epfl.drawyourpath.userProfile.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoalEntity
import com.epfl.drawyourpath.challenge.milestone.MilestoneEntity
import com.epfl.drawyourpath.path.cache.PointsEntity
import com.epfl.drawyourpath.path.cache.RunEntity

@Dao
interface UserDao {

    /**
     * returns a read-only user
     * @param id the id of the user
     * @return [LiveData] of [UserEntity]
     */
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: String): LiveData<UserEntity?>

    /**
     * insert user and its daily goals and runs
     * @param user the user to insert
     * @param dailyGoals the daily goals
     * @param milestones the milestones
     * @param runs the runs
     * @param points the points of the runs
     */
    @Transaction
    fun insertAll(user: UserEntity, dailyGoals: List<DailyGoalEntity>, milestones: List<MilestoneEntity>, runs: List<RunEntity>, points: List<PointsEntity>) {
        if (update(user) != 1) {
            insertUser(user)
        }
        insertAllDailyGoals(dailyGoals)
        insertAllMilestones(milestones)
        insertAllRuns(runs)
        insertAllPoints(points)
    }

    /**
     * insert a new user inside the room database and will replace if there is a conflict with the id
     * @param user the user to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    /**
     * insert a new dailyGoal inside the room database and will replace if there is a conflict with the id and date
     * @param dailyGoals the dailyGoal to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDailyGoals(dailyGoals: List<DailyGoalEntity>)

    /**
     * insert the milestones inside the cache
     * @param milestones the milestones
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMilestones(milestones: List<MilestoneEntity>)

    /**
     * insert the runs inside the cache
     * @param runs the runs
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllRuns(runs: List<RunEntity>)

    /**
     * insert the points inside the cache
     * @param points the points
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllPoints(points: List<PointsEntity>)

    /**
     * update the user with new data
     * @param user the user to update
     */
    @Update
    fun update(user: UserEntity): Int

    /**
     * set a new username to user with the corresponding id
     * @param id the id of the user
     * @param username the new username of the user
     */
    @Query("UPDATE User SET username = :username WHERE id = :id")
    fun updateUsername(id: String, username: String)

    /**
     * set a new profile photo to user with the corresponding id
     * @param id the id of the user
     * @param photo the new profile photo of the user
     */
    @Query("UPDATE User SET photo = :photo WHERE id = :id")
    fun updatePhoto(id: String, photo: ByteArray?)

    /**
     * delete the user from the room database
     * @param user the user to delete
     */
    @Delete
    fun delete(user: UserEntity)

    /**
     * delete all the users
     */
    @Query("DELETE FROM User")
    fun clear()
}
