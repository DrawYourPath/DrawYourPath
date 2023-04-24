package com.epfl.drawyourpath.path.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.userProfile.cache.UserEntity

@Entity(
    tableName = "Run",
    primaryKeys = ["user_id", "start_time"],
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE)],
)
data class RunEntity(
    @ColumnInfo("user_id")
    val userId: String,

    @ColumnInfo("start_time")
    val startTime: Long,

    @ColumnInfo("end_time")
    val endTime: Long
)