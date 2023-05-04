package com.epfl.drawyourpath.path.cache

import androidx.room.*

@Dao
interface RunDao {

    /**
     * TODO change run history in usermodelcache
     * return a map of runs and its points
     * @param userId the user id
     * @return [LiveData] of [RunEntity] and [PointsEntity]
     */
    /*@Query("SELECT * FROM Run JOIN Points ON Run.user_id = Points.user_id AND Run.start_time = Points.run_id WHERE Run.user_id = :userId ORDER BY Run.start_time DESC")
    fun getAllRunsAndPoints(userId: String): LiveData<Map<RunEntity, List<PointsEntity>>>*/

    /**
     * return a map of runs and its points
     * @param userId the user id
     * @return a map of [RunEntity] and [PointsEntity]
     */
    @Query("SELECT * FROM Run JOIN Points ON Run.user_id = Points.user_id AND Run.start_time = Points.run_id WHERE Run.user_id = :userId ORDER BY Run.start_time DESC")
    fun getAllRunsAndPoints(userId: String): Map<RunEntity, List<PointsEntity>>

    /**
     * update to know if the run is synced with the firebase
     * @param userId the user id
     * @param runId the run id
     * @param sync the syncing status
     */
    @Query("UPDATE Run SET sync = :sync WHERE user_id = :userId AND start_time = :runId")
    fun runSynced(userId: String, runId: Long, sync: Boolean = true)

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
