package com.epfl.drawyourpath.challenge.milestone

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.userProfile.cache.UserEntity

@Entity(
    tableName = "Milestone",
    primaryKeys = ["user_id", "milestone"],
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE)],
)
data class MilestoneEntity(

    @ColumnInfo("user_id")
    val userId: String,

    val milestone: String,

    val date: Long,
) {

    constructor(userId: String, milestone: Milestone) : this(
        userId,
        milestone.name,
        milestone.date.toEpochDay(),
    )

}
