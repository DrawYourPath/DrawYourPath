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
    @Query("SELECT * FROM Run JOIN Points ON Run.user_id = Points.user_id AND Run.start_time = Points.run_id WHERE Run.user_id = :userId ORDER BY Run.start_time DESC")
    fun getAllRunsAndPoints(userId: String): LiveData<Map<RunEntity, List<PointsEntity>>>

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