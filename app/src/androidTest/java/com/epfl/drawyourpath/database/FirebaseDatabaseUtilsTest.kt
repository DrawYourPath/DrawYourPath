package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FirebaseDatabaseUtilsTest {

    private fun mockNumberSnapshot(value: Number?): DataSnapshot {
        val snap = mock(DataSnapshot::class.java)
        `when`(snap.value).thenReturn(value)
        return snap
    }

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

    private fun mockRun(run: Run): DataSnapshot {
        val snapshot = mock(DataSnapshot::class.java)

        val path = mock(DataSnapshot::class.java)
        val pointsSnap = mock(DataSnapshot::class.java)

        val points = run.getPath().getPoints().map { mockPoint(it) }

        `when`(path.child("points")).thenReturn(pointsSnap)
        `when`(pointsSnap.children).thenReturn(points)

        val startTime = mockNumberSnapshot(run.getStartTime())
        `when`(snapshot.child("startTime")).thenReturn(startTime)

        val endTime = mockNumberSnapshot(run.getEndTime())
        `when`(snapshot.child("endTime")).thenReturn(endTime)
        `when`(snapshot.child("path")).thenReturn(path)

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
    fun transformRunReturnsExpectedRuns() {
        val runs = listOf(
            Run(
                Path(
                    listOf(
                        LatLng(1.0, 1.0),
                        LatLng(2.0, 2.0),
                    ),
                ),
                1000,
                2000,
            ),
            Run(
                Path(
                    listOf(
                        LatLng(12.0, 12.0),
                        LatLng(22.0, 22.0),
                    ),
                ),
                4000,
                8000,
            ),
        )

        val runsSnaps = runs.map { mockRun(it) }

        val snapshot = mock(DataSnapshot::class.java)

        `when`(snapshot.children).thenReturn(runsSnaps)

        val transformedRuns = FirebaseDatabaseUtils.transformRuns(snapshot)

        assertThat(runs.size, `is`(transformedRuns.size))
    }
}
