package com.epfl.drawyourpath.database

import org.junit.Assert
import org.junit.Test

class FirebaseDatabaseUtilsTest {

    @Test
    fun transformDailyGoalsReturnsEmptyListWithNullSnapshot() {
        Assert.assertTrue(FirebaseDatabaseUtils.transformDailyGoals(null).isEmpty())
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
