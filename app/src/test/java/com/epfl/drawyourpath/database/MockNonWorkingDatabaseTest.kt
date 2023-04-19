package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.userProfile.UserModel
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
        mock.updateUsername("").assertError(true)
        mock.setUsername("").assertError(true)
        mock.initUserProfile(mockUser).assertError(true)
        mock.getUserAccount("").assertError(mockUser)
        mock.getLoggedUserAccount().assertError(mockUser)
        mock.setDistanceGoal(0.0).assertError(true)
        mock.setActivityTimeGoal(0.0).assertError(true)
        mock.setNbOfPathsGoal(0).assertError(true)
        mock.addUserToFriendsList("").assertError(Unit)
        mock.removeUserFromFriendlist("").assertError(Unit)
    }

    private fun <T> CompletableFuture<T>.assertError(ret: T) {
        this.exceptionally {
            assertEquals("java.lang.Error: ${MockNonWorkingDatabase.ERROR_NAME}", it.message)
            ret
        }.get(2, TimeUnit.SECONDS)
    }

}