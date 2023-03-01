package com.github.drawyourpath.bootcamp.webapi.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BoredActivityEntity::class], version = 1)
abstract class BoredActivityDatabase : RoomDatabase() {
    abstract fun BoredActivityDao(): BoredActivityDao

    companion object {
        const val NAME: String = "BoredActivity.sqlite"
    }
}