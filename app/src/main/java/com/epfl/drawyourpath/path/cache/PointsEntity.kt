package com.epfl.drawyourpath.path.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.userProfile.cache.UserEntity

@Entity(
    tableName = "Points",
    primaryKeys = ["user_id", "run_id", "index"],
    foreignKeys = [ForeignKey(
        entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE
    ), ForeignKey(entity = RunEntity::class, parentColumns = ["start_time"], childColumns = ["run_id"], onDelete = ForeignKey.CASCADE)],
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
