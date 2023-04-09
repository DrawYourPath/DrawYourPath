package com.epfl.drawyourpath.userProfile.cache

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    /**
     * returns a read-only user
     * @param id the id of the user
     * @return [LiveData] of [UserData]
     */
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: String): LiveData<UserData>

    /**
     * insert a new user inside the room database and will replace if there is a conflict with the id
     * @param user the user to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserData)

    /**
     * update the user with new data
     * @param user the user to update
     */
    @Update
    fun update(user: UserData)

    /**
     * set a new username to user with the corresponding id
     * @param id the id of the user
     * @param username the new username of the user
     */
    @Query("UPDATE User SET username = :username WHERE id = :id")
    fun updateUsername(id: String, username: String)

    /**
     * set a new distance goal to user with the corresponding id
     * @param id the id of the user
     * @param distanceGoal the new distance goal of the user
     */
    @Query("UPDATE User SET distance_goal = :distanceGoal WHERE id = :id")
    fun updateDistanceGoal(id: String, distanceGoal: Double)

    /**
     * set a new time goal to user with the corresponding id
     * @param id the id of the user
     * @param timeGoal the new time goal of the user
     */
    @Query("UPDATE User SET time_goal = :timeGoal WHERE id = :id")
    fun updateTimeGoal(id: String, timeGoal: Double)

    /**
     * set a new number of paths goal to user with the corresponding id
     * @param id the id of the user
     * @param pathsGoal the new number of paths goal of the user
     */
    @Query("UPDATE User SET paths_goal = :pathsGoal WHERE id = :id")
    fun updatePathsGoal(id: String, pathsGoal: Int)

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
    fun delete(user: UserData)

    /**
     * delete all the users
     */
    @Query("DELETE FROM User")
    fun clear()
}