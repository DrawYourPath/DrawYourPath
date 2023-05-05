package com.epfl.drawyourpath.database

import android.util.Log
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate

object FirebaseDatabaseUtils {

    /**
     * Helper function to obtain the daily goal list from the database of the user
     * @param data the data snapshot containing the daily goal list
     * @return a list containing the daily goal realized by the user
     */
    fun transformDailyGoals(data: DataSnapshot?): List<DailyGoal> {
        return data?.children?.mapNotNull {
            if (it.key == null) {
                Log.w(this::class.java.name, "Daily goal's key was null.")
                null
            } else {
                val date = LocalDate.ofEpochDay(it.key!!.toLong())

                val expectedDistance: Double? =
                    it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE)
                        .getValue(Double::class.java)
                val expectedTime: Double? =
                    it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME)
                        .getValue(Double::class.java)
                val expectedPaths: Int? =
                    (it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS).value as Number?)?.toInt()
                val distance: Double? =
                    it.child(FirebaseKeys.GOAL_HISTORY_DISTANCE).getValue(Double::class.java)
                val time: Double? =
                    it.child(FirebaseKeys.GOAL_HISTORY_TIME).getValue(Double::class.java)
                val paths: Int? =
                    (it.child(FirebaseKeys.GOAL_HISTORY_PATHS).value as Number?)?.toInt()

                DailyGoal(
                    expectedDistance = expectedDistance ?: 0.0,
                    expectedPaths = expectedPaths ?: 0,
                    expectedTime = expectedTime ?: 0.0,
                    distance = distance ?: 0.0,
                    time = time ?: 0.0,
                    paths = paths ?: 0,
                    date = date,
                )
            }
        } ?: emptyList()
    }

    /**
     * Helper function to obtain a list from the keys of a database snapshot.
     * @param data the data snapshot to be converted to a list
     * @return a list containing the keys of the snapshot
     */
    fun getKeys(data: DataSnapshot?): List<String> {
        return data?.children?.map { it.key as String } ?: emptyList()
    }

    /**
     * Helper function to obtain the runs history from the database of the user
     * @param data the data snapshot containing the history
     * @return a list containing the history of the runs of the user
     */
    fun transformRuns(data: DataSnapshot?): List<Run> {
        return data?.children?.mapNotNull {
            transformRun(it)
        } ?: emptyList()
    }

    /**
     * Helper function to retrieve a run object from the database
     * @param data the datasnapshot containing the run
     * @return the run corresponding to the data
     */
    fun transformRun(data: DataSnapshot): Run? {
        val points = data.child("path").child("points").children.mapNotNull {
            val lat = it.child("latitude").getValue(Double::class.java)
            val lon = it.child("longitude").getValue(Double::class.java)
            if (lat != null && lon != null) {
                LatLng(lat, lon)
            } else {
                null
            }
        }

        val startTime = data.child("startTime").value as? Long
        val endTime = data.child("endTime").value as? Long
        if (startTime != null && endTime != null) {
            return Run(Path(points), startTime, endTime)
        }
        return null
    }

    /**
     * Helper function to obtain the chats list from the database of the user
     * @param data the data snapshot containing the chats List
     * @return a list containing the conversationId of all the chats where the user is present
     */
    fun transformChatList(data: DataSnapshot?): List<String> {
        return data?.children?.mapNotNull { it.key } ?: emptyList()
    }
}
