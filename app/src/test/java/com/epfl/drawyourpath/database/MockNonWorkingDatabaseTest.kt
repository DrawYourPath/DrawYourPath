package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.challenge.DailyGoal
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MockNonWorkingDatabaseTest {

    @Test
    fun everyFunctionShouldThrowError() {
        val mock = MockNonWorkingDatabase()
        val mockUser = MockDataBase().userModelTest

        mock.isUserStoredInDatabase("").assertError(true)
        mock.getUsernameFromUserId("").assertError("")
        mock.getUserIdFromUsername("").assertError("")
        mock.isUsernameAvailable("").assertError(true)
        mock.updateUsername("").assertError(Unit)
        mock.setUsername("").assertError(Unit)
        mock.initUserProfile(mockUser).assertError(Unit)
        mock.getUserAccount("").assertError(mockUser)
        mock.getLoggedUserAccount().assertError(mockUser)
        mock.setCurrentDistanceGoal(0.0).assertError(Unit)
        mock.setCurrentActivityTimeGoal(0.0).assertError(Unit)
        mock.setCurrentNbOfPathsGoal(0).assertError(Unit)
        mock.addUserToFriendsList("").assertError(Unit)
        mock.removeUserFromFriendlist("").assertError(Unit)
        mock.addDailyGoal(DailyGoal(0.0, 0.0, 0)).assertError(Unit)
        mock.updateUserAchievements(0.0, 0.0).assertError(Unit)
    }

    private fun <T> CompletableFuture<T>.assertError(ret: T) {
        this.exceptionally {
            assertEquals("java.lang.Error: ${MockNonWorkingDatabase.ERROR_NAME}", it.message)
            ret
        }.get(2, TimeUnit.SECONDS)
    }
}
