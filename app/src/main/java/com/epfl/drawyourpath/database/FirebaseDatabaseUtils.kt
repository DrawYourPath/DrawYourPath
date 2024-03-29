package com.epfl.drawyourpath.database

import android.util.Log
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.milestone.MilestoneEnum
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.HashMap

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
        val sections = data?.child(FirebaseKeys.RUN_PATH)?.child(FirebaseKeys.PATH_POINTS)?.children?.map { section ->
            section.children.mapNotNull { point ->
                val lat = getNumber(point.child(FirebaseKeys.POINT_LATITUDE))?.toDouble()
                val lon = getNumber(point.child(FirebaseKeys.POINT_LONGITUDE))?.toDouble()
                if (lat != null && lon != null) {
                    LatLng(lat, lon)
                } else {
                    Log.w(this::class.java.name, "A coordinate was badly formatted.")
                    null
                }
            }
        } ?: emptyList()

        val startTime = getNumber(data?.child(FirebaseKeys.RUN_START_TIME))?.toLong()
        val duration = getNumber(data?.child(FirebaseKeys.RUN_DURATION))?.toLong()
        val endTime = getNumber(data?.child(FirebaseKeys.RUN_END_TIME))?.toLong()
        val predictedShape = (data?.child(FirebaseKeys.RUN_SHAPE)?.value ?: "None") as String
        val similarityScore = (getNumber(data?.child(FirebaseKeys.RUN_SCORE)) ?: 0.0).toDouble()
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
     * Helper function to obtain the tournaments ids from the database
     * @param data the data snapshot containing the tournaments ids
     * @return a list containing the tournaments ids
     */
    fun transformTournamentIdList(data: DataSnapshot?): List<String> {
        return data?.children?.mapNotNull {
            it.key as String
        } ?: emptyList()
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
     * Helper function to obtain the votes of a post from the database
     * @param data the data snapshot containing the votes
     * @return a list containing the votes of a post
     */
    fun transformVotesToMap(data: DataSnapshot?): Map<String, Int> {
        val votes = mutableMapOf<String, Int>()
        data?.children?.mapNotNull {
            votes.put(it.key as String, (it.value as Long).toInt())
        }
        return votes
    }

    /**
     * Helper function to obtain a post from a database snapshot
     * @param data the data snapshot containing the post
     * @return the post, or null if an error occurred
     */
    fun transformPost(data: DataSnapshot?): TournamentPost? {
        val postId = data?.child(FirebaseKeys.POST_ID)?.value as String?
        val tournamentId = data?.child(FirebaseKeys.POST_TOURNAMENT_ID)?.value as String?
        val tournamentName = data?.child(FirebaseKeys.POST_TOURNAMENT_NAME)?.value as String?
        val userId = data?.child(FirebaseKeys.POST_USER_ID)?.value as String?
        val run = transformRun(data?.child(FirebaseKeys.POST_RUN))
        val date = transformLocalDateTime(data?.child(FirebaseKeys.POST_DATE))
        val usersVotes = transformVotesToMap(data?.child(FirebaseKeys.POST_USERS_VOTES))

        if (postId == null || tournamentId == null || tournamentName == null || userId == null || run == null || date == null) {
            Log.e(this::class.java.name, "TournamentPost had null values")
            return null
        }

        return TournamentPost(postId, tournamentId, tournamentName, userId, run, date, usersVotes.toMutableMap())
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

        if (year == null || month == null || dayOfMonth == null ||
            hour == null || minute == null || second == null || nano == null
        ) {
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
     * @return a future that contains the user Model
     */
    fun mapToUserData(data: DataSnapshot, userId: String): UserData {
        val profile = data.child(FirebaseKeys.PROFILE)
        val goals = data.child(FirebaseKeys.GOALS)
        val tournaments = data.child(FirebaseKeys.USER_TOURNAMENTS)

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
            chatList = transformChatList(profile.child(FirebaseKeys.USER_CHATS)),
            tournaments = transformTournamentIdList(tournaments),
            trophies = transformTrophyFromData(data.child(FirebaseKeys.TROPHIES)),
            milestones = transformMilestoneFromData(data.child(FirebaseKeys.MILESTONES)),

        )
    }

    /**
     * Helper function to convert a data snapshot to a tournament
     * @param data the data snapshot containing the tournament
     * @return the tournament, or null if an error occurred
     */
    fun mapToTournament(data: DataSnapshot?): Tournament? {
        val tournamentInfo = mapToTournamentInfo(data?.child(FirebaseKeys.TOURNAMENT_INFO))
        val participants = getKeys(data?.child(FirebaseKeys.TOURNAMENT_PARTICIPANTS_IDS))
        val posts = transformPostList(data?.child(FirebaseKeys.TOURNAMENT_POSTS))

        if (tournamentInfo == null) {
            Log.e(this::class.java.name, "Tournament had null values")
            return null
        }

        return tournamentInfo.copy(participants = participants, posts = posts)
    }

    /**
     * Helper function to convert a data snapshot to a tournament
     * @param data the data snapshot containing the tournament info
     * @return the tournament info, or null if an error occurred
     */
    fun mapToTournamentInfo(data: DataSnapshot?): Tournament? {
        val id = data?.child(FirebaseKeys.TOURNAMENT_ID)?.value as String?
        val name = data?.child(FirebaseKeys.TOURNAMENT_NAME)?.value as String?
        val description = data?.child(FirebaseKeys.TOURNAMENT_DESCRIPTION)?.value as String?
        val creatorId = data?.child(FirebaseKeys.TOURNAMENT_CREATOR_ID)?.value as String?
        val startDate = transformLocalDateTime(data?.child(FirebaseKeys.TOURNAMENT_START_DATE))
        val endDate = transformLocalDateTime(data?.child(FirebaseKeys.TOURNAMENT_END_DATE))
        val visibilityString = data?.child(FirebaseKeys.TOURNAMENT_VISIBILITY)?.value as String?

        // Get the visibility back to an enum value
        val visibility = try {
            if (visibilityString != null) {
                Tournament.Visibility.valueOf(visibilityString)
            } else {
                null
            }
        } catch (_: IllegalArgumentException) {
            Log.e(this::class.java.name, "Tournament visibility had an unknown value")
            return null
        }

        if (id == null || name == null || description == null || creatorId == null ||
            startDate == null || endDate == null || visibility == null
        ) {
            Log.e(this::class.java.name, "Tournament had null values")
            return null
        }

        return Tournament(
            id = id,
            name = name,
            description = description,
            creatorId = creatorId,
            startDate = startDate,
            endDate = endDate,
            visibility = visibility,
        )
    }

    /**
     * Helper function to transform a trophy object into an object to store in the database
     * The tournament id is not take into account since is used as a key for the trophy in the database.
     * @param trophy to store in the database
     * @return a map that can be stored in Firebase Database
     */
    fun transformTrophyToData(trophy: Trophy): HashMap<String, Any> {
        return hashMapOf(
            FirebaseKeys.TROPHY_TOURNAMENT_NAME to trophy.tournamentName,
            FirebaseKeys.TROPHY_TOURNAMENT_DESCRIPTION to trophy.tournamentDescription,
            FirebaseKeys.TROPHY_DATE to trophy.date.toEpochDay(),
            FirebaseKeys.TROPHY_RANKING to trophy.ranking,
        )
    }

    /**
     * Helper function to obtain the trophies list from the database of the user
     * @param data the data snapshot containing the trophies list
     * @return a list containing the trophies of the user
     */
    fun transformTrophyFromData(data: DataSnapshot?): List<Trophy> {
        return data?.children?.mapNotNull {
            if (it.key == null) {
                Log.w(this::class.java.name, "Tophies's key was null.")
                null
            } else {
                val tournamentId = it.key!!.toString()

                val tournamentName: String =
                    it.child(FirebaseKeys.TROPHY_TOURNAMENT_NAME).value as String
                val tournamentDescription: String =
                    it.child(FirebaseKeys.TROPHY_TOURNAMENT_DESCRIPTION).value as String
                val dateLong: Long =
                    it.child(FirebaseKeys.TROPHY_DATE).value as Long
                val ranking: Int =
                    it.child(FirebaseKeys.TROPHY_RANKING).value as Int

                Trophy(
                    tournamentId = tournamentId,
                    tournamentName = tournamentName,
                    tournamentDescription = tournamentDescription,
                    date = LocalDate.ofEpochDay(dateLong),
                    ranking = ranking,
                )
            }
        } ?: emptyList()
    }

    /**
     * Helper function to transform a milestone object enum with a date into an object to store in the database
     * The tournament id is not take into account since is used as a key for the trophy in the database.
     * @param milestone the milestone object enum to store
     * @param date the date to store alongside the milestone
     * @return a map linking the milestone's name to the day of the date.
     */
    fun transformMilestoneToData(milestone: MilestoneEnum, date: LocalDate): HashMap<String, Any> {
        return hashMapOf(
            milestone.name to date.toEpochDay(),
        )
    }

    /**
     * Helper function to obtain the milestones list from the database of the user
     * @param data the data snapshot containing the milestone list
     * @return a list containing the milestone of the user
     */
    fun transformMilestoneFromData(data: DataSnapshot?): List<MilestoneData> {
        return data?.children?.mapNotNull {
            if (it.key == null) {
                Log.w(this::class.java.name, "Milestone's key was null.")
                null
            } else {
                val milestone = it.key!!.toString()
                val dateLong: Long =
                    it.value as Long

                MilestoneData(
                    milestone = MilestoneEnum.valueOf(milestone),
                    date = LocalDate.ofEpochDay(dateLong),
                )
            }
        } ?: emptyList()
    }
}
