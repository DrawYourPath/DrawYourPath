package com.epfl.drawyourpath.userProfile.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalEntity

@Dao
interface UserDao {

    /**
     * returns a read-only user
     * @param id the id of the user
     * @return [LiveData] of [UserEntity]
     */
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: String): LiveData<UserEntity>

    @Transaction
    fun insertAll(user: UserEntity, dailyGoals: List<DailyGoalEntity>) {
        insertUser(user)
        insertAllDailyGoal(dailyGoals)
    }

    /**
     * insert a new user inside the room database and will replace if there is a conflict with the id
     * @param user the user to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    /**
     * insert a new user inside the room database and abort if it already exist inside the room database
     * @param user the user to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserIfEmpty(user: UserEntity)

    /**
     * insert a new dailyGoal inside the room database and will replace if there is a conflict with the id and date
     * @param dailyGoal the dailyGoal to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDailyGoal(dailyGoal: List<DailyGoalEntity>)

    /**
     * update the user with new data
     * @param user the user to update
     */
    @Update
    fun update(user: UserEntity)

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