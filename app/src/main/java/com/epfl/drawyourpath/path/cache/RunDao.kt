package com.epfl.drawyourpath.path.cache

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    /**
     * return a map of runs and its points
     * @param userId the user id
     * @return [LiveData] of [RunEntity] and [PointsEntity]
     */
    @Query("SELECT * FROM Run JOIN Points ON Run.user_id = Points.user_id WHERE Run.user_id = :userId")
    fun getAllRunAndPoints(userId: String): LiveData<Map<RunEntity, List<PointsEntity>>>

    /**
     * insert the run with its path inside the cache
     * @param run the run
     * @param points the list of points
     */
    @Transaction
    fun insert(run: RunEntity, points: List<PointsEntity>) {
        insertRun(run)
        insertAllPoints(points)
    }

    /**
     * should not be used: use [insert] instead
     * insert the run inside the cache
     * @param run the run
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: RunEntity)

    /**
     * should not be used: use [insert] instead
     * insert the points inside the cache
     * @param points the points
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPoints(points: List<PointsEntity>)

    /**
     * delete the run from the room database
     * @param run the user to delete
     */
    @Delete
    fun delete(run: RunEntity)

    /**
     * delete all the runs
     */
    @Query("DELETE FROM Run")
    fun clear()

}