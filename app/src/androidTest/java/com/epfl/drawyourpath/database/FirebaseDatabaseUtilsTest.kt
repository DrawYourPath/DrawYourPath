package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FirebaseDatabaseUtilsTest {

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

        val doubleSnapshot = mock(DataSnapshot::class.java)
        `when`(doubleSnapshot.getValue(Double::class.java)).thenReturn(12.0)

        val goal1 = mock(DataSnapshot::class.java)
        `when`(goal1.key).thenReturn("123456")
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE)).thenReturn(doubleSnapshot)
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME)).thenReturn(doubleSnapshot)
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS)).thenReturn(doubleSnapshot)
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_DISTANCE)).thenReturn(doubleSnapshot)
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_TIME)).thenReturn(doubleSnapshot)
        `when`(goal1.child(FirebaseKeys.GOAL_HISTORY_PATHS)).thenReturn(doubleSnapshot)

        val snapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.children).thenReturn(listOf(goal1))
        assertThat(FirebaseDatabaseUtils.transformDailyGoals(snapshot).size, `is`(1))
    }

    @Test
    fun getKeysOfNullSnapshotIsEmpty() {
        Assert.assertEquals(FirebaseDatabaseUtils.getKeys(null).size, 0)
    }

    @Test
    fun transformChatListOfNullSnapshotIsEmpty() {
        Assert.assertEquals(FirebaseDatabaseUtils.transformChatList(null).size, 0)
    }

    @Test
    fun transformRunReturnsNullForNullSnapshot() {
        Assert.assertEquals(FirebaseDatabaseUtils.transformRun(null), null)
    }
}
