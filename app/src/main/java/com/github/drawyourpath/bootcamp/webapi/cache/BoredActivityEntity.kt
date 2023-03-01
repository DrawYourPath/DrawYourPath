package com.github.drawyourpath.bootcamp.webapi.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoredActivityEntity(
    @PrimaryKey
    val key: Int,
    @ColumnInfo(name = "activity")
    val activity: String?,
    @ColumnInfo(name = "type")
    val type: String?,
    @ColumnInfo(name = "participants")
    val participants: Int?,
){
    override fun toString(): String {
        return buildString {
            append(activity)
            append('\n')
            append("Type : ")
            append(type)
            append('\n')
            append(participants)
            append(" participants")
        }
    }
}
