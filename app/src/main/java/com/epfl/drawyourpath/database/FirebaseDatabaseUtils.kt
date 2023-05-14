package com.epfl.drawyourpath.database

import android.util.Log
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.utils.Utils
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate

object FirebaseDatabaseUtils {

    private fun getNumber(snapshot: DataSnapshot?): Number? {
        return snapshot?.value as Number?
    }

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
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE))?.toDouble()
                val expectedTime: Double? =
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME))?.toDouble()
                val expectedPaths: Int? =
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS))?.toInt()
                val distance: Double? =
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_DISTANCE))?.toDouble()
                val time: Double? =
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_TIME))?.toDouble()
                val paths: Int? =
                    getNumber(it.child(FirebaseKeys.GOAL_HISTORY_PATHS))?.toInt()

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
        return data?.children?.mapNotNull { it.key as String } ?: emptyList()
    }

    /**
     * Helper function to obtain the runs history from the database of the user
     * @param data the data snapshot containing the history
     * @return a list containing the history of the runs of the user
     */
    fun transformRunList(data: DataSnapshot?): List<Run> {
        return data?.children?.mapNotNull {
            transformRun(it)
        } ?: emptyList()
    }

    /**
     * Helper function to retrieve a run object from the database.
     * @param data the datasnapshot containing the run
     * @return the run corresponding to the data
     */
    fun transformRun(data: DataSnapshot?): Run? {
        // TODO: THis function must be refectored with the new strcuture of a run and a path
        /*
        val points = data?.child("path")?.child("points")?.children?.mapNotNull {
            val lat = getNumber(it.child("latitude"))?.toDouble()
            val lon = getNumber(it.child("longitude"))?.toDouble()
            if (lat != null && lon != null) {
                LatLng(lat, lon)
            } else {
                Log.w(this::class.java.name, "A coordinate was badly formatted.")
                null
            }
        } ?: emptyList()

        val startTime = getNumber(data?.child("startTime"))?.toLong()
        val endTime = getNumber(data?.child("endTime"))?.toLong()
        if (startTime != null && endTime != null) {
            return Run(Path(points), startTime, endTime)
        }

        Log.e(this::class.java.name, "Run time was null.")
        */
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

    /**
     * // TODO: We shouldn't throw when data is badly formatted.
     * Helper function to transform some data into a message
     * @param data datasnpshot that contains the data of the message
     * @return a message correposnding to this data
     */
    fun transformMessage(data: DataSnapshot): Message {
        val dateStr: String =
            data.key ?: throw Exception("There content of this data not correspond to a message")
        val date = dateStr.toLong()
        val sender = data.child(FirebaseKeys.CHAT_MESSAGE_SENDER).value as String
        val dataImage = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE)
        val dataRun = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN)
        val dataText = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT)
        if (dataImage.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.Picture(Utils.decodePhoto(dataImage.value as String)!!),
                timestamp = date,
            )
        }
        if (dataRun.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.RunPath(
                    transformRun(
                        dataRun.children.first(),
                    )!!,
                ),
                timestamp = date,
            )
        }

        if (dataText.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.Text(dataText.value as String),
                timestamp = date,
            )
        }

        throw Error("The content of the message correspond to any type !")
    }

    /**
     * Helper function to convert a data snapshot to a userModel
     * @param data data snapshot to convert
     * @param userId of the user
     * @return ta future that contains the user Model
     */
    fun mapToUserData(data: DataSnapshot, userId: String): UserData {
        val profile = data.child(FirebaseKeys.PROFILE)
        val goals = data.child(FirebaseKeys.GOALS)

        return UserData(
            userId = userId,
            username = profile.child(FirebaseKeys.USERNAME).value as String?,
            firstname = profile.child(FirebaseKeys.FIRSTNAME).value as String?,
            surname = profile.child(FirebaseKeys.SURNAME).value as String?,
            birthDate = profile.child(FirebaseKeys.BIRTHDATE).value as Long?,
            email = profile.child(FirebaseKeys.EMAIL).value as String?,
            picture = profile.child(FirebaseKeys.PICTURE).value as String?,
            friendList = getKeys(profile.child(FirebaseKeys.FRIENDS)),
            goals = UserGoals(
                paths = (goals.child(FirebaseKeys.GOAL_PATH).value as Number?)?.toLong(),
                distance = (goals.child(FirebaseKeys.GOAL_DISTANCE).value as Number?)?.toDouble(),
                activityTime = (goals.child(FirebaseKeys.GOAL_TIME).value as Number?)?.toDouble(),
            ),
            runs = transformRunList(data.child(FirebaseKeys.RUN_HISTORY)),
            dailyGoals = transformDailyGoals(data.child(FirebaseKeys.DAILY_GOALS)),
            chatList = transformChatList(data.child(FirebaseKeys.USER_CHATS)),
        )
    }
}
