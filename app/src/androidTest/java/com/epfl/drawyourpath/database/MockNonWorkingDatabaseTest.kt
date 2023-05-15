package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MockNonWorkingDatabaseTest {

    @Test
    fun everyFunctionShouldThrowError() {
        val mock = MockNonWorkingDatabase()
        val mockUser = MockDatabase.mockUser
        val mockTournament = MockDatabase().mockTournament
        val mockChatPreview = MockDatabase().MOCK_CHAT_PREVIEWS[0]
        val mockChatMembers = MockDatabase().MOCK_CHAT_MEMBERS[0].membersList!!
        val mockChatMessages = MockDatabase().MOCK_CHAT_MESSAGES[0].chat!!

        mock.isUserInDatabase("").assertError(true)
        mock.isTournamentInDatabase("").assertError(true)
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
        mock.createChatConversation("", emptyList(), "", "").assertError("")
        mock.getChatPreview("").assertError(mockChatPreview)
        mock.setChatTitle("", "").assertError(Unit)
        mock.getChatMemberList("").assertError(mockChatMembers)
        mock.addChatMember("", "").assertError(Unit)
        mock.removeChatMember("", "").assertError(Unit)
        mock.getChatMessages("").assertError(mockChatMessages)
        mock.addChatMessage("", mockChatMessages.get(0)).assertError(Unit)
        mock.removeChatMessage("", 0L).assertError(Unit)
        mock.modifyChatTextMessage("", 0L, "").assertError(Unit)
        mock.setUserData("", UserData()).assertError(Unit)
        mock.addRunToHistory("", Run(Path(), 1, 9, 10)).assertError(Unit)
        mock.removeRunFromHistory("", Run(Path(), 1, 9, 10)).assertError(Unit)
        mock.addTournament(mockTournament).assertError(Unit)
        mock.removeTournament("").assertError(Unit)
        mock.addUserToTournament("", "").assertError(Unit)
        mock.removeUserFromTournament("", "").assertError(Unit)
    }

    /**
     * Test if getTournamentUID() returns null (as it doesn't return a future)
     */
    @Test
    fun getTournamentUIDReturnsNull() {
        val mock = MockNonWorkingDatabase()
        assertEquals(null, mock.getTournamentUID())
    }

    private fun <T> CompletableFuture<T>.assertError(ret: T) {
        this.exceptionally {
            assertEquals("java.lang.Error: ${MockNonWorkingDatabase.ERROR_NAME}", it.message)
            ret
        }.get(2, TimeUnit.SECONDS)
    }
}
