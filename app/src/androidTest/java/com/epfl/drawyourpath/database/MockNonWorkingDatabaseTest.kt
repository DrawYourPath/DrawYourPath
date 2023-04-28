package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MockNonWorkingDatabaseTest {

    @Test
    fun everyFunctionShouldThrowError() {
        val mock = MockNonWorkingDatabase()
        val mockUser = MockDatabase().mockUser
        val mockChatPreview = MockDatabase().MOCK_CHAT_PREVIEWS[0]

        mock.isUserInDatabase("").assertError(true)
        mock.getUsername("").assertError("")
        mock.getUserIdFromUsername("").assertError("")
        mock.isUsernameAvailable("").assertError(true)
        mock.setUsername("", "").assertError(Unit)
        mock.setUserData("", UserData()).assertError(Unit)
        mock.getUserData("").assertError(mockUser)
        mock.setGoals("", UserGoals()).assertError(Unit)
        mock.addFriend("", "").assertError(Unit)
        mock.removeFriend("", "").assertError(Unit)
        mock.addDailyGoal("", DailyGoal(0.0, 0.0, 0)).assertError(Unit)
        mock.updateUserAchievements("", 0.0, 0.0).assertError(Unit)
        mock.createChatConversation("", emptyList(), "").assertError(Unit)
        mock.getChatPreview("").assertError(mockChatPreview)
    }

    private fun <T> CompletableFuture<T>.assertError(ret: T) {
        this.exceptionally {
            assertEquals("java.lang.Error: ${MockNonWorkingDatabase.ERROR_NAME}", it.message)
            ret
        }.get(2, TimeUnit.SECONDS)
    }
}
