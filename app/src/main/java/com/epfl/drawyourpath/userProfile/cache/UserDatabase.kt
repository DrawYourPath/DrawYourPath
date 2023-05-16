package com.epfl.drawyourpath.userProfile.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epfl.drawyourpath.path.cache.PointsEntity
import com.epfl.drawyourpath.path.cache.RunDao
import com.epfl.drawyourpath.path.cache.RunEntity
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalDao
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalEntity

@Database(entities = [UserEntity::class, DailyGoalEntity::class, RunEntity::class, PointsEntity::class], version = 7)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun dailyGoalDao(): DailyGoalDao

    abstract fun runDao(): RunDao

    companion object {
        const val NAME = "UserDatabase"
    }
}
