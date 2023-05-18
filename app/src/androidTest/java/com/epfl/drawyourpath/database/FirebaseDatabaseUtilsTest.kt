package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class FirebaseDatabaseUtilsTest {

    private fun <T> mockSnapshot(value: T?): DataSnapshot {
        val snap = mock(DataSnapshot::class.java)
        `when`(snap.value).thenReturn(value)
        return snap
    }

    private fun mockNumberSnapshot(value: Number?): DataSnapshot = mockSnapshot(value)

    private fun mockDailyGoalSnapshot(dailyGoal: DailyGoal): DataSnapshot {
        // NOTE: Can't mock inside `thenReturn`.
        val goal = mock(DataSnapshot::class.java)
        `when`(goal.key).thenReturn(dailyGoal.date.toEpochDay().toString())
        val edist = mockNumberSnapshot(dailyGoal.expectedDistance)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE))
            .thenReturn(edist)
        val etime = mockNumberSnapshot(dailyGoal.expectedTime)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME))
            .thenReturn(etime)
        val epaths = mockNumberSnapshot(dailyGoal.expectedPaths)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS))
            .thenReturn(epaths)
        val dist = mockNumberSnapshot(dailyGoal.distance)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_DISTANCE))
            .thenReturn(dist)
        val time = mockNumberSnapshot(dailyGoal.time)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_TIME))
            .thenReturn(time)
        val paths = mockNumberSnapshot(dailyGoal.paths)
        `when`(goal.child(FirebaseKeys.GOAL_HISTORY_PATHS))
            .thenReturn(paths)
        return goal
    }

    private fun mockPoint(point: LatLng): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val lat = mockNumberSnapshot(point.latitude)
        `when`(snapshot.child("latitude")).thenReturn(lat)
        val long = mockNumberSnapshot(point.longitude)
        `when`(snapshot.child("longitude")).thenReturn(long)

        return snapshot
    }

    private fun mockSection(section: List<LatLng>): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val pointsSnap = section.map {
            mockPoint(it)
        }
        `when`(snapshot.children).thenReturn(pointsSnap)

        return snapshot
    }

    private fun mockRun(run: Run): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val path = mock(DataSnapshot::class.java)
        val pathPoints = mock(DataSnapshot::class.java)
        val sectionsSnap = run.getPath().getPoints().map {
            mockSection(it)
        }

        `when`(path.child("points")).thenReturn(pathPoints)
        `when`(pathPoints.children).thenReturn(sectionsSnap)

        `when`(snapshot.child("path")).thenReturn(path)

        val startTime = mockNumberSnapshot(run.getStartTime())
        `when`(snapshot.child("startTime")).thenReturn(startTime)

        val duration = mockNumberSnapshot(run.getDuration())
        `when`(snapshot.child("duration")).thenReturn(duration)

        val endTime = mockNumberSnapshot(run.getEndTime())
        `when`(snapshot.child("endTime")).thenReturn(endTime)

        val predictedShape = mockSnapshot(run.predictedShape)
        `when`(snapshot.child("predictedShape")).thenReturn(predictedShape)

        val similarityScore = mockNumberSnapshot(run.similarityScore)
        `when`(snapshot.child("similarityScore")).thenReturn(similarityScore)

        return snapshot
    }

    private fun mockLocalDateTime(ldt: LocalDateTime): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val year = mockNumberSnapshot(ldt.year)
        `when`(snapshot.child("year")).thenReturn(year)
        val month = mockNumberSnapshot(ldt.monthValue)
        `when`(snapshot.child("monthValue")).thenReturn(month)
        val dayOfMonth = mockNumberSnapshot(ldt.dayOfMonth)
        `when`(snapshot.child("dayOfMonth")).thenReturn(dayOfMonth)
        val hour = mockNumberSnapshot(ldt.hour)
        `when`(snapshot.child("hour")).thenReturn(hour)
        val minute = mockNumberSnapshot(ldt.minute)
        `when`(snapshot.child("minute")).thenReturn(minute)
        val second = mockNumberSnapshot(ldt.second)
        `when`(snapshot.child("second")).thenReturn(second)
        val nano = mockNumberSnapshot(ldt.nano)
        `when`(snapshot.child("nano")).thenReturn(nano)

        return snapshot
    }

    private fun mockTournamentPost(post: TournamentPost): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val userId = mockSnapshot(post.userId)
        `when`(snapshot.child("userId")).thenReturn(userId)
        val run = mockRun(post.run)
        `when`(snapshot.child("run")).thenReturn(run)
        val votes = mockNumberSnapshot(post.getVotes())
        `when`(snapshot.child("votes")).thenReturn(votes)
        val date = mockLocalDateTime(post.date)
        `when`(snapshot.child("date")).thenReturn(date)
        val usersVotes = mockSnapshot(post.getUsersVotes())
        `when`(snapshot.child("usersVotes")).thenReturn(usersVotes)

        return snapshot
    }

    private fun mockTournamentPostList(posts: List<TournamentPost>): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val postsSnap = posts.map {
            mockTournamentPost(it)
        }
        `when`(snapshot.children).thenReturn(postsSnap)

        return snapshot
    }

    private fun mockTournament(tournament: Tournament): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val id = mockSnapshot(tournament.id)
        `when`(snapshot.child("id")).thenReturn(id)

        val name = mockSnapshot(tournament.name)
        `when`(snapshot.child("name")).thenReturn(name)

        val description = mockSnapshot(tournament.description)
        `when`(snapshot.child("description")).thenReturn(description)

        val creatorId = mockSnapshot(tournament.creatorId)
        `when`(snapshot.child("creatorId")).thenReturn(creatorId)

        val startDate = mockLocalDateTime(tournament.startDate)
        `when`(snapshot.child("startDate")).thenReturn(startDate)

        val endDate = mockLocalDateTime(tournament.endDate)
        `when`(snapshot.child("endDate")).thenReturn(endDate)

        val participants = mock(DataSnapshot::class.java)
        val participantsSubNodes = tournament.participants.map {
            val participant = mock(DataSnapshot::class.java)
            `when`(participant.value).thenReturn(true)
            `when`(participant.key).thenReturn(it)
            participant
        }
        `when`(snapshot.child("participants")).thenReturn(participants)
        `when`(participants.children).thenReturn(participantsSubNodes)

        val posts = mockTournamentPostList(tournament.posts)
        `when`(snapshot.child("posts")).thenReturn(posts)

        val visibility = mockSnapshot(tournament.visibility.name)
        `when`(snapshot.child("visibility")).thenReturn(visibility)

        return snapshot
    }

    private fun mockMessage(message: Message): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        `when`(snapshot.key).thenReturn(message.id.toString())
        val sendSnap = mockSnapshot(message.senderId)
        `when`(snapshot.child(FirebaseKeys.CHAT_MESSAGE_SENDER)).thenReturn(sendSnap)

        val image = mockSnapshot(
            if (message.content is MessageContent.Picture) {
                Utils.encodePhotoToString((message.content as MessageContent.Picture).image)
            } else {
                null
            },
        )
        `when`(snapshot.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE)).thenReturn(image)

        val run = if (message.content is MessageContent.RunPath) {
            val runSubnode = mock(DataSnapshot::class.java)
            val runs = listOf(mockRun((message.content as MessageContent.RunPath).run))
            `when`(runSubnode.children).thenReturn(runs)
            `when`(runSubnode.value).thenReturn(true)
            runSubnode
        } else {
            mockSnapshot(null)
        }
        `when`(snapshot.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN)).thenReturn(run)

        val txt = mockSnapshot(
            if (message.content is MessageContent.Text) {
                (message.content as MessageContent.Text).text
            } else {
                null
            },
        )
        `when`(snapshot.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT)).thenReturn(txt)

        return snapshot
    }

    @Test
    fun transformDailyGoalsReturnsEmptyListWithNullSnapshot() {
        assertTrue(FirebaseDatabaseUtils.transformDailyGoals(null).isEmpty())
    }

    @Test
    fun transformDailyGoalsReturnsEmptyListWhenNoChildren() {
        val snapshot = mock(DataSnapshot::class.java)
        assertThat(FirebaseDatabaseUtils.transformDailyGoals(snapshot).isEmpty(), `is`(true))
    }

    @Test
    fun transformDailyGoalsReturnsGoals() {
        val dailyGoals = listOf(
            DailyGoal(12.0, 12.0, 12),
            DailyGoal(10.0, 10.0, 10),
        )

        val dailyGoalsSnapshots = dailyGoals.map { mockDailyGoalSnapshot(it) }

        val snapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.children).thenReturn(dailyGoalsSnapshots)

        dailyGoals.zip(FirebaseDatabaseUtils.transformDailyGoals(snapshot)).forEach {
            assertThat(it.first, `is`(it.second))
        }
    }

    @Test
    fun getKeysOfNullSnapshotIsEmpty() {
        assertEquals(FirebaseDatabaseUtils.getKeys(null).size, 0)
    }

    @Test
    fun getKeysReturnsSnapshotKeys() {
        val snapshot = mock(DataSnapshot::class.java)
        val child1 = mock(DataSnapshot::class.java)
        val child2 = mock(DataSnapshot::class.java)

        `when`(child1.key).thenReturn("child1")
        `when`(child2.key).thenReturn("child2")
        `when`(snapshot.children).thenReturn(listOf(child1, child2))

        assertThat(FirebaseDatabaseUtils.getKeys(snapshot), `is`(listOf("child1", "child2")))
    }

    @Test
    fun transformChatListOfNullSnapshotIsEmpty() {
        assertEquals(FirebaseDatabaseUtils.transformChatList(null).size, 0)
    }

    @Test
    fun transformChatListOfEmptySnapshotIsEmpty() {
        val chatList = mock(DataSnapshot::class.java)
        `when`(chatList.children).thenReturn(emptyList())
        assertEquals(FirebaseDatabaseUtils.transformChatList(chatList).size, 0)
    }

    @Test
    fun transformRunReturnsNullForNullSnapshot() {
        assertEquals(FirebaseDatabaseUtils.transformRun(null), null)
    }

    @Test
    fun transformRunsReturnsEmptyListForEmptySnapshot() {
        assertEquals(FirebaseDatabaseUtils.transformRunList(null).size, 0)
    }

    @Test
    fun transformRunListReturnExpectedData() {
        val runs = listOf(
            Run(
                Path(
                    listOf(
                        listOf(
                            LatLng(1.0, 1.0),
                            LatLng(2.1, 2.0),
                        ),
                        listOf(
                            LatLng(3.0, 3.0),
                            LatLng(0.0, 3.1),
                        ),
                    ),
                ),
                startTime = 1000,
                duration = 1000,
                endTime = 2000,
                predictedShape = "Cat",
                similarityScore = 0.9,
            ),
            Run(
                Path(
                    listOf(
                        listOf(
                            LatLng(12.0, 12.0),
                            LatLng(31.98, -98.45),
                            LatLng(4.0, -4.0),
                        ),
                    ),
                ),
                startTime = 4000,
                duration = 4000,
                endTime = 8000,
            ),
        )

        val runsSnaps = runs.map { mockRun(it) }

        val snapshot = mock(DataSnapshot::class.java)

        `when`(snapshot.children).thenReturn(runsSnaps)

        val transformedRuns = FirebaseDatabaseUtils.transformRunList(snapshot)

        assertThat(runs.size, `is`(transformedRuns.size))
    }

    @Test
    fun transformLocalDateTimeReturnsExpectedData() {
        val ldt = LocalDateTime.now()
        val ldtSnapshot = mockLocalDateTime(ldt)

        assertThat(FirebaseDatabaseUtils.transformLocalDateTime(ldtSnapshot), `is`(ldt))
    }

    @Test
    fun transformLocalDateTimeReturnsNullWhenDataSnapshotContainsNullValues() {
        assertEquals(FirebaseDatabaseUtils.transformLocalDateTime(null), null)
    }

    @Test
    fun transformPostReturnsExpectedData() {
        val post = TournamentPost(
            userId = "user1",
            run = Run(
                Path(listOf(listOf(LatLng(0.1, 1.0), LatLng(1.1, 0.2)))),
                10,
                10,
                20,
                "Lion",
                0.8,
            ),
            votes = 2,
            date = LocalDateTime.now().minusDays(2),
            usersVotes = mutableMapOf("user1" to 1, "user2" to 1),
        )
        val postSnapshot = mockTournamentPost(post)
        val transformedPost = FirebaseDatabaseUtils.transformPost(postSnapshot)
        assertThat(transformedPost!!.userId, `is`(post.userId))
        // Just to know if the run is the same one
        assertThat(transformedPost.run.getCalories(), `is`(post.run.getCalories()))
        assertThat(transformedPost.getVotes(), `is`(post.getVotes()))
        assertThat(transformedPost.date, `is`(post.date))
        assertThat(transformedPost.getUsersVotes(), `is`(post.getUsersVotes()))
    }

    @Test
    fun transformPostReturnsNullWhenNullSnapshot() {
        assertEquals(FirebaseDatabaseUtils.transformPost(null), null)
    }

    @Test
    fun transformPostListReturnsExpectedData() {
        val posts = listOf(
            TournamentPost(
                userId = "user1",
                run = Run(
                    Path(listOf(listOf(LatLng(0.1, 1.0), LatLng(1.1, 0.2)))),
                    10,
                    10,
                    20,
                    "Lion",
                    0.8,
                ),
                votes = 2,
                date = LocalDateTime.now().minusDays(2),
                usersVotes = mutableMapOf("user1" to 1, "user2" to 1),
            ),
            TournamentPost(
                "user2",
                run = Run(
                    Path(listOf(listOf(LatLng(0.1, 1.0), LatLng(1.1, 0.2)), listOf(LatLng(9.9, 10.11)))),
                    30,
                    20,
                    50,
                ),
                votes = 0,
                date = LocalDateTime.now().minusDays(3),
                usersVotes = mutableMapOf("user1" to -1, "user2" to 1),
            ),
        )
        val postsSnapshot = posts.map { mockTournamentPost(it) }
        val snapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.children).thenReturn(postsSnapshot)
        val transformedPosts = FirebaseDatabaseUtils.transformPostList(snapshot)

        assertThat(transformedPosts.size, `is`(posts.size))
    }

    @Test
    fun transformPostListReturnsEmptyListWithNullSnapshot() {
        assertEquals(FirebaseDatabaseUtils.transformPostList(null), emptyList<TournamentPost>())
    }

    @Test
    fun transformTextMessageReturnsExpectedData() {
        val message = Message(
            id = 20,
            senderId = "foobar",
            content = MessageContent.Text("Hello World"),
            timestamp = 20,
        )

        val transMessage = FirebaseDatabaseUtils.transformMessage(mockMessage(message))

        assertThat(message.id, `is`(transMessage.id))
        assertThat(message.content, `is`(transMessage.content))
        assertThat(message.timestamp, `is`(transMessage.timestamp))
        assertThat(message.senderId, `is`(transMessage.senderId))
    }

    @Test
    fun transformRunMessageReturnsExpectedData() {
        val message = Message(
            id = 20,
            senderId = "foobar",
            content = MessageContent.RunPath(
                Run(
                    Path(
                        listOf(
                            listOf(
                                LatLng(1.0, 1.0),
                                LatLng(2.0, 2.0),
                            ),
                        ),
                    ),
                    startTime = 1000,
                    duration = 1000,
                    endTime = 2000,
                ),
            ),
            timestamp = 20,
        )

        val transMessage = FirebaseDatabaseUtils.transformMessage(mockMessage(message))

        assertThat(message.id, `is`(transMessage.id))
        assertThat(message.timestamp, `is`(transMessage.timestamp))
        assertThat(message.senderId, `is`(transMessage.senderId))
    }

    @Test
    fun transformRunPictureReturnsExpectedData() {
        val message = Message(
            id = 20,
            senderId = "foobar",
            content = MessageContent.Picture(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)),
            timestamp = 20,
        )

        val transMessage = FirebaseDatabaseUtils.transformMessage(mockMessage(message))

        assertThat(message.id, `is`(transMessage.id))
        assertThat(message.timestamp, `is`(transMessage.timestamp))
        assertThat(message.senderId, `is`(transMessage.senderId))
    }

    @Test
    fun mapToUserDataReturnsExpectedValue() {
        val userData = UserData(
            userId = "uid",
            username = "uname",
            birthDate = 12,
            email = "foobar",
        )

        val userDataSnapshot = FirebaseDatabaseTest.mockParent(
            mapOf(
                FirebaseKeys.PROFILE to FirebaseDatabaseTest.mockParent(
                    mapOf(
                        FirebaseKeys.USERNAME to FirebaseDatabaseTest.mockSnapshot(userData.username),
                        FirebaseKeys.EMAIL to FirebaseDatabaseTest.mockSnapshot(userData.email),
                        FirebaseKeys.FIRSTNAME to FirebaseDatabaseTest.mockSnapshot(userData.firstname),
                        FirebaseKeys.BIRTHDATE to FirebaseDatabaseTest.mockSnapshot(userData.birthDate),
                        FirebaseKeys.SURNAME to FirebaseDatabaseTest.mockSnapshot(userData.surname),
                        FirebaseKeys.BIRTHDATE to FirebaseDatabaseTest.mockSnapshot(userData.birthDate),
                        FirebaseKeys.PICTURE to FirebaseDatabaseTest.mockSnapshot(userData.picture),
                        FirebaseKeys.FRIENDS to FirebaseDatabaseTest.mockSnapshot(null),
                    ),
                ),
                FirebaseKeys.GOALS to FirebaseDatabaseTest.mockSnapshot(null),
                FirebaseKeys.DAILY_GOALS to FirebaseDatabaseTest.mockSnapshot(null),
                FirebaseKeys.RUN_HISTORY to FirebaseDatabaseTest.mockSnapshot(null),
                FirebaseKeys.USER_CHATS to FirebaseDatabaseTest.mockSnapshot(null),
            ),
        )

        val resData = FirebaseDatabaseUtils.mapToUserData(userDataSnapshot, userData.userId!!)

        assertThat(resData.userId, `is`(userData.userId))
        assertThat(resData.username, `is`(userData.username))
        assertThat(resData.email, `is`(userData.email))
        assertThat(resData.birthDate, `is`(userData.birthDate))
    }

    @Test
    fun mapToTournamentReturnsExpectedValue() {
        val posts = listOf(
            TournamentPost(
                userId = "user1",
                run = Run(
                    Path(listOf(listOf(LatLng(0.1, 1.0), LatLng(1.1, 0.2)))),
                    10,
                    10,
                    20,
                    "Lion",
                    0.8,
                ),
                votes = 2,
                date = LocalDateTime.now().minusDays(2),
                usersVotes = mutableMapOf("user1" to 1, "user2" to 1),
            ),
            TournamentPost(
                "user2",
                run = Run(
                    Path(listOf(listOf(LatLng(0.1, 1.0), LatLng(1.1, 0.2)), listOf(LatLng(9.9, 10.11)))),
                    30,
                    20,
                    50,
                ),
                votes = 0,
                date = LocalDateTime.now().minusDays(3),
                usersVotes = mutableMapOf("user1" to -1, "user2" to 1),
            ),
        )
        val tournament = Tournament(
            id = "testId",
            name = "testName",
            description = "testDescription",
            creatorId = "testCreatorId",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusWeeks(1),
            participants = listOf("user1", "user2"),
            posts = posts,
            visibility = Tournament.Visibility.PUBLIC,
        )

        val tournamentDataSnapshot = mockTournament(tournament)
        val reformedTournament = FirebaseDatabaseUtils.mapToTournament(tournamentDataSnapshot)

        assertThat(reformedTournament!!.id, `is`(tournament.id))
        assertThat(reformedTournament.name, `is`(tournament.name))
        assertThat(reformedTournament.description, `is`(tournament.description))
        assertThat(reformedTournament.creatorId, `is`(tournament.creatorId))
        assertThat(reformedTournament.startDate, `is`(tournament.startDate))
        assertThat(reformedTournament.endDate, `is`(tournament.endDate))
        assertThat(reformedTournament.participants, `is`(tournament.participants))
        assertThat(reformedTournament.posts.size, `is`(tournament.posts.size))
        assertThat(reformedTournament.visibility, `is`(tournament.visibility))
    }
}
