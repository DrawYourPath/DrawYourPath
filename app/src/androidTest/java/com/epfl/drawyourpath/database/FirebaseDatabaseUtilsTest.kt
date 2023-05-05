package com.epfl.drawyourpath.database

import org.junit.Assert
import org.junit.Test

class FirebaseDatabaseUtilsTest {

    @Test
    fun transformDailyGoalsReturnsEmptyListWithNullSnapshot() {
        Assert.assertTrue(FirebaseDatabaseUtils.transformDailyGoals(null).isEmpty())
    }
}
