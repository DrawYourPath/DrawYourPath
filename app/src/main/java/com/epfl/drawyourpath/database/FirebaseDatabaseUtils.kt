package com.epfl.drawyourpath.database

import android.util.Log
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate
import java.time.LocalDateTime

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
        val sections = data?.child("path")?.child("points")?.children?.map { section ->
            section.children.mapNotNull { point ->
                val lat = getNumber(point.child("latitude"))?.toDouble()
                val lon = getNumber(point.child("longitude"))?.toDouble()
                if (lat != null && lon != null) {
                    LatLng(lat, lon)
                } else {
                    Log.w(this::class.java.name, "A coordinate was badly formatted.")
                    null
                }
            }
        } ?: emptyList()

        val startTime = getNumber(data?.child("startTime"))?.toLong()
        val duration = getNumber(data?.child("duration"))?.toLong()
        val endTime = getNumber(data?.child("endTime"))?.toLong()
        val predictedShape = (data?.child("predictedShape")?.value ?: "None") as String
        val similarityScore = (getNumber(data?.child("similarityScore")) ?: 0.0).toDouble()
        if (startTime == null) {
            Log.e(this::class.java.name, "Run start time was null.")
            return null
        }
        if (endTime == null) {
            Log.e(this::class.java.name, "Run end time was null.")
            return null
        }
        if (duration == null) {
            Log.e(this::class.java.name, "Run duration was null.")
            return null
        }
        return Run(
            Path(sections),
            startTime,
            duration,
            endTime,
            predictedShape,
            similarityScore,
        )
    }

    /**
     * Helper function to obtain the posts of a tournament from the database
     * @param data the data snapshot containing the posts
     * @return a list containing the posts of the tournament
     */
    fun transformPostList(data: DataSnapshot?): List<TournamentPost> {
        return data?.children?.mapNotNull {
            transformPost(it)
        } ?: emptyList()
    }

    /**
     * Helper function to obtain a post from a database snapshot
     * @param data the data snapshot containing the post
     * @return the post, or null if an error occurred
     */
    fun transformPost(data: DataSnapshot?): TournamentPost? {
        val userId = data?.child("userId")?.value as String?
        val run = transformRun(data?.child("run"))
        val votes = getNumber(data?.child("votes"))?.toInt()
        val date = transformLocalDateTime(data?.child("date"))
        // Unchecked cast here but should work without problem
        val usersVotes = (data?.child("usersVotes")?.value ?: emptyMap<String, Int>()) as Map<String, Int>

        if (userId == null) {
            Log.e(this::class.java.name, "TournamentPost had null userId")
            return null
        }
        if (run == null) {
            Log.e(this::class.java.name, "TournamentPost had null run")
            return null
        }
        if (votes == null) {
            Log.e(this::class.java.name, "TournamentPost had null votes")
            return null
        }
        if (date == null) {
            Log.e(this::class.java.name, "TournamentPost had null date")
            return null
        }

        return TournamentPost(userId, run, votes, date, usersVotes.toMutableMap())
    }

    /**
     * Helper function to obtain an object LocalDateTime from a data snapshot
     * @param data the data snapshot containing the LocalDateTime
     * @return the LocalDateTime, or null if an error occurred
     */
    fun transformLocalDateTime(data: DataSnapshot?): LocalDateTime? {
        val year = getNumber(data?.child("year"))?.toInt()
        val month = getNumber(data?.child("monthValue"))?.toInt()
        val dayOfMonth = getNumber(data?.child("dayOfMonth"))?.toInt()
        val hour = getNumber(data?.child("hour"))?.toInt()
        val minute = getNumber(data?.child("minute"))?.toInt()
        val second = getNumber(data?.child("second"))?.toInt()
        val nano = getNumber(data?.child("nano"))?.toInt()

        if (year == null || month == null ||dayOfMonth == null || hour == null || minute == null ||
                second == null || nano == null) {
            Log.e(this::class.java.name, "LocalDateTime had null values")
            return null
        }

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nano)
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

    /**
     * Helper function to convert a data snapshot to a tournament
     * @param data the data snapshot containing the tournament
     * @return the tournament, or null if an error occurred
     */
    fun mapToTournament(data: DataSnapshot): Tournament? {
        val id = data.child(FirebaseKeys.TOURNAMENT_ID).value as String?
        val name = data.child(FirebaseKeys.TOURNAMENT_NAME).value as String?
        val description = data.child(FirebaseKeys.TOURNAMENT_DESCRIPTION).value as String?
        val creatorId = data.child(FirebaseKeys.TOURNAMENT_CREATOR_ID).value as String?
        val startDate = transformLocalDateTime(data.child(FirebaseKeys.TOURNAMENT_START_DATE))
        val endDate = transformLocalDateTime(data.child(FirebaseKeys.TOURNAMENT_END_DATE))
        val participants = getKeys(data.child(FirebaseKeys.TOURNAMENT_PARTICIPANTS_IDS))
        val posts = transformPostList(data.child(FirebaseKeys.TOURNAMENT_POSTS))
        val visibilityString = data.child(FirebaseKeys.TOURNAMENT_VISIBILITY).value as String?

        if (id == null || name == null || description == null || creatorId == null || startDate == null
            || endDate == null || visibilityString == null) {
            Log.e(this::class.java.name, "Tournament had null values")
            return null
        }

        val visibility: Tournament.Visibility
        try {
            visibility = Tournament.Visibility.valueOf(visibilityString)
        } catch (_: IllegalArgumentException) {
            Log.e(this::class.java.name, "Tournament visibility has unknow value")
            return null
        }

        return Tournament(
            id,
            name,
            description,
            creatorId,
            startDate,
            endDate,
            participants,
            posts,
            visibility
        )
    }
}
