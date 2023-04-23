package com.epfl.drawyourpath.userProfile.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalDao
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalEntity

@Database(entities = [UserEntity::class, DailyGoalEntity::class], version = 3)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        const val NAME = "UserDatabase"
    }
}
