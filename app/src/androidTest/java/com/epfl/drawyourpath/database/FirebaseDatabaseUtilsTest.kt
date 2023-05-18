package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.milestone.MilestoneEnum
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.database.FirebaseDatabaseUtils.transformMilestoneToData
import com.epfl.drawyourpath.database.FirebaseDatabaseUtils.transformTrophyToData
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

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

    private fun mockTrophiesSnapshot(trophy: Trophy): DataSnapshot {
        // NOTE: Can't mock inside `thenReturn`.
        val trophyData = mock(DataSnapshot::class.java)
        `when`(trophyData.key).thenReturn(trophy.tournamentId)
        val tournamentName = mockSnapshot(trophy.tournamentName)
        `when`(trophyData.child(FirebaseKeys.TROPHY_TOURNAMENT_NAME))
            .thenReturn(tournamentName)
        val tournamentDescription = mockSnapshot(trophy.tournamentDescription)
        `when`(trophyData.child(FirebaseKeys.TROPHY_TOURNAMENT_DESCRIPTION))
            .thenReturn(tournamentDescription)
        val date = mockNumberSnapshot(trophy.date.toEpochDay())
        `when`(trophyData.child(FirebaseKeys.TROPHY_DATE))
            .thenReturn(date)
        val ranking = mockNumberSnapshot(trophy.ranking)
        `when`(trophyData.child(FirebaseKeys.TROPHY_RANKING))
            .thenReturn(ranking)
        return trophyData
    }

    private fun mockMilestonesSnapshot(milestone: MilestoneData): DataSnapshot {
        // NOTE: Can't mock inside `thenReturn`.
        val milestoneData = mock(DataSnapshot::class.java)
        `when`(milestoneData.key).thenReturn(milestone.milestone!!.name)
        `when`(milestoneData.value).thenReturn(milestone.date!!.toEpochDay())
        return milestoneData
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
        Assert.assertEquals(FirebaseDatabaseUtils.getKeys(null).size, 0)
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
        Assert.assertEquals(FirebaseDatabaseUtils.transformChatList(null).size, 0)
    }

    @Test
    fun transformChatListOfEmptySnapshotIsEmpty() {
        val chatList = mock(DataSnapshot::class.java)
        `when`(chatList.children).thenReturn(emptyList())
        Assert.assertEquals(FirebaseDatabaseUtils.transformChatList(chatList).size, 0)
    }

    @Test
    fun transformRunReturnsNullForNullSnapshot() {
        Assert.assertEquals(FirebaseDatabaseUtils.transformRun(null), null)
    }

    @Test
    fun transformRunsReturnsEmptyListForEmptySnapshot() {
        assertEquals(FirebaseDatabaseUtils.transformRunList(null).size, 0)
    }

    @Test
    fun transformRunListReturnExpectedRunsInOrder() {
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
                FirebaseKeys.TROPHIES to FirebaseDatabaseTest.mockSnapshot(null),
                FirebaseKeys.MILESTONES to FirebaseDatabaseTest.mockSnapshot(null),
                FirebaseKeys.USER_CHATS to FirebaseDatabaseTest.mockSnapshot(null),
            ),
        )

        val resData = FirebaseDatabaseUtils.mapToUserData(userDataSnapshot, userData.userId!!)

        assertThat(resData.userId, `is`(userData.userId))
        assertThat(resData.username, `is`(userData.username))
        assertThat(resData.email, `is`(userData.email))
        assertThat(resData.birthDate, `is`(userData.birthDate))
    }

    /**
     * Test if a given trophy is correctly transform to the correct hashmap
     */
    @Test
    fun transformTrophyToDataCorrectly() {
        val trophy = Trophy(tournamentId = "123", tournamentName = "tournament", tournamentDescription = "description", date = LocalDate.of(2000, 2, 20), ranking = 3)
        val expectedMap = hashMapOf<String, Any>(
            FirebaseKeys.TROPHY_TOURNAMENT_NAME to "tournament",
            FirebaseKeys.TROPHY_TOURNAMENT_DESCRIPTION to "description",
            FirebaseKeys.TROPHY_DATE to LocalDate.of(2000, 2, 20).toEpochDay(),
            FirebaseKeys.TROPHY_RANKING to 3,
        )
        assertEquals(expectedMap, transformTrophyToData(trophy))
    }

    /**
     * Test that transform null snapshot returns an empty trophies list
     */
    @Test
    fun transformTrophiesReturnsEmptyListWithNullSnapshot() {
        assertTrue(FirebaseDatabaseUtils.transformTrophyFromData(null).isEmpty())
    }

    /**
     * Test that transform a data snapshot with no trophies return an empty trophies list
     */
    @Test
    fun transformTrophiesReturnsEmptyListWhenNoChildren() {
        val snapshot = mock(DataSnapshot::class.java)
        assertThat(FirebaseDatabaseUtils.transformTrophyFromData(snapshot).isEmpty(), `is`(true))
    }

    /**
     * Test that transform a data snapshot that contains trophies return the correct list of trophies
     */
    @Test
    fun transformTrophiesReturnsTrophies() {
        val trophies = listOf(
            Trophy("123", "tournament1", "description1", LocalDate.of(2000, 2, 20), 3),
            Trophy("345", "tournament2", "description2", LocalDate.of(2000, 2, 21), 2),
        )

        val trophySnapshot = trophies.map { mockTrophiesSnapshot(it) }

        val snapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.children).thenReturn(trophySnapshot)

        trophies.zip(FirebaseDatabaseUtils.transformTrophyFromData(snapshot)).forEach {
            assertThat(it.first, `is`(it.second))
        }
    }

    /**
     * Test if a given milestone is correctly transform to the correct hashmap
     */
    @Test
    fun transformMilestoneToDataCorrectly() {
        val expectedMap = hashMapOf<String, Any>(
            "THE_FIRST_KILOMETER" to LocalDate.of(2000, 2, 20).toEpochDay(),
        )
        assertEquals(expectedMap, transformMilestoneToData(MilestoneEnum.THE_FIRST_KILOMETER, LocalDate.of(2000, 2, 20)))
    }

    /**
     * Test that transform null snapshot returns an empty milestones list
     */
    @Test
    fun transformMilestonesReturnsEmptyListWithNullSnapshot() {
        assertTrue(FirebaseDatabaseUtils.transformMilestoneFromData(null).isEmpty())
    }

    /**
     * Test that transform a data snapshot with no milestones return an empty milestones list
     */
    @Test
    fun transformMilestonesReturnsEmptyListWhenNoChildren() {
        val snapshot = mock(DataSnapshot::class.java)
        assertThat(FirebaseDatabaseUtils.transformMilestoneFromData(snapshot).isEmpty(), `is`(true))
    }

    /**
     * Test that transform a data snapshot that contains milestones return the correct list of milestones
     */
    @Test
    fun transformMilestonesReturnsMilestones() {
        val milestonesData = listOf(
            MilestoneData(milestone = MilestoneEnum.THE_FIRST_KILOMETER, date = LocalDate.of(2000, 2, 20)),
            MilestoneData(milestone = MilestoneEnum.HUNDRED_KILOMETERS, date = LocalDate.of(2000, 2, 21)),
        )

        val milestoneSnapshot = milestonesData.map { mockMilestonesSnapshot(it) }

        val snapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.children).thenReturn(milestoneSnapshot)

        milestonesData.zip(FirebaseDatabaseUtils.transformMilestoneFromData(snapshot)).forEach {
            assertThat(it.first, `is`(it.second))
        }
    }
}
