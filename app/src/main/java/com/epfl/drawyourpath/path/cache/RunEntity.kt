package com.epfl.drawyourpath.path.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.cache.UserEntity
import com.google.android.gms.maps.model.LatLng

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

    val duration: Long,

    @ColumnInfo("end_time")
    val endTime: Long,

    @ColumnInfo("shape")
    val predictedShape: String,

    @ColumnInfo("score")
    val similarityScore: Double,

    val sync: Boolean,
) {

    companion object {
        /**
         * transform a list of run into entities used in the cache
         * @param userId the user id
         * @param runs the list of runs to transform
         * @param sync with the firebase
         * @return a list of pair of [RunEntity] and [PointsEntity]
         */
        fun fromRunsToEntities(userId: String, runs: List<Run>, sync: Boolean = true): List<Pair<RunEntity, List<PointsEntity>>> {
            return runs.map { run ->
                Pair(
                    RunEntity(
                        userId = userId,
                        startTime = run.getStartTime(),
                        duration = run.getDuration(),
                        endTime = run.getEndTime(),
                        predictedShape = run.predictedShape,
                        similarityScore = run.similarityScore,
                        sync = sync
                    ),
                    fromPathToEntity(userId, run.getStartTime(), run.getPath()),
                )
            }
        }

        /**
         * transform a path into entities used in the cache
         * @param userId the user id
         * @param runId the run id
         * @param path the path to transform
         * @return a list [PointsEntity]
         */
        private fun fromPathToEntity(userId: String, runId: Long, path: Path): List<PointsEntity> {
            return path.getPoints().mapIndexed { sectionIndex, section ->
                section.mapIndexed { index, point ->
                    PointsEntity(
                        userId = userId,
                        runId = runId,
                        section = sectionIndex,
                        index = index,
                        latitude = point.latitude,
                        longitude = point.longitude
                    )
                }
            }.flatten()
        }

        /**
         * transform an entity into a run
         * @param runEntity the run to transform
         * @param pointsEntity the points of the path related to the run
         * @return the run
         */
        fun fromEntityToRun(runEntity: RunEntity, pointsEntity: List<PointsEntity>): Run {
            return Run(
                path = fromEntityToPath(pointsEntity.filter { it.runId == runEntity.startTime }),
                startTime = runEntity.startTime,
                duration = runEntity.duration,
                endTime = runEntity.endTime,
                predictedShape = runEntity.predictedShape,
                similarityScore = runEntity.similarityScore,
            )
        }

        /**
         * transform an entity into a path
         * @param pointsEntity the points of the path
         * @return the path
         */
        private fun fromEntityToPath(pointsEntity: List<PointsEntity>): Path {
            val sections = pointsEntity.distinctBy { it.section }.map { it.section }.sortedBy { it }
            val points = sections.map { section ->
                pointsEntity.filter { it.section == section }.sortedBy { it.index }.map { LatLng(it.latitude, it.longitude) }
            }
            return Path(points)
        }
    }
}
