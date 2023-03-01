package com.github.drawyourpath.bootcamp.webapi.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface BoredActivityDao {
    @Query("SELECT * FROM BoredActivityEntity")
    fun getAll(): List<BoredActivityEntity>

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg BoredActivityEntity: BoredActivityEntity)

    @Query("DELETE FROM BoredActivityEntity")
    fun clear()


}