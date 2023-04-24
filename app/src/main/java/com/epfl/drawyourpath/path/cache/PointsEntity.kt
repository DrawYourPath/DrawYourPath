package com.epfl.drawyourpath.path.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Points",
    primaryKeys = ["user_id", "run_id", "index"],
    foreignKeys = [ForeignKey(entity = RunEntity::class, parentColumns = ["user_id", "start_time"], childColumns = ["user_id", "run_id"], onDelete = ForeignKey.CASCADE)],
)
data class PointsEntity(
    @ColumnInfo("user_id")
    val userId: String,

    @ColumnInfo("run_id")
    val runId: Long,

    val index: Int,

    @ColumnInfo("lat")
    val latitude: Double,

    @ColumnInfo("lon")
    val longitude: Double
)
