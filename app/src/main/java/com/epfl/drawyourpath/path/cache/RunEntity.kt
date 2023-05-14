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
    @ColumnInfo("user_id") val userId: String,

    @ColumnInfo("start_time") val startTime: Long,

    @ColumnInfo("end_time") val endTime: Long,

    val sync: Boolean,
) {
    // TODO: will be refactor since the structure of a path/run have changed
    /*
    companion object {
        /**
         * transform a list of run into entities used in the cache
         * @param userId the user id
         * @param runs the list of runs to transform
         * @param sync with the firebase
         * @return a list of pair of entities
         */
        fun fromRunsToEntities(userId: String, runs: List<Run>, sync: Boolean = true): List<Pair<RunEntity, List<PointsEntity>>> {
            return runs.map { run ->
                Pair(
                    RunEntity(userId, run.getStartTime(), run.getEndTime(), sync),
                    run.getPath().getPoints().mapIndexed { index, point ->
                        PointsEntity(
                            userId,
                            run.getStartTime(),
                            index,
                            point.latitude,
                            point.longitude,
                        )
                    },
                )
            }
        }

        /**
         * transform an entity into a run
         * @param runEntity the run to transform
         * @param pointsEntity the points of the path related to the run
         * @return the run
         */
        fun fromEntityToRun(runEntity: RunEntity, pointsEntity: List<PointsEntity>): Run {
            return Run(Path(pointsEntity.sortedBy { it.index }.map { LatLng(it.latitude, it.longitude) }), runEntity.startTime, runEntity.endTime)
        }
    }
    */
}
