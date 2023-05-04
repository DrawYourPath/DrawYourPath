package com.epfl.drawyourpath.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.streams.toList

class MockDatabaseTest {
    private val mockDatabase = MockDatabase()

    private val userIdTest: String = MockAuth.MOCK_USER.getUid()
    private val userAuthTest: User = MockAuth.MOCK_USER
    private val usernameTest: String = mockDatabase.mockUser.username!!
    private val distanceGoalTest: Double = mockDatabase.mockUser.goals!!.distance!!
    private val activityTimeGoalTest: Double = mockDatabase.mockUser.goals!!.activityTime!!.toDouble()
    private val nbOfPathsGoalTest: Int = mockDatabase.mockUser.goals!!.paths!!.toInt()
    private val firstnameTest = mockDatabase.mockUser.firstname!!
    private val surnameTest = mockDatabase.mockUser.surname!!
    private val takenUsername = mockDatabase.MOCK_USERS[1].username!!
    private val dateOfBirthTest = mockDatabase.mockUser.birthDate!!
    private val runHistoryTest = mockDatabase.mockUser.runs!!

    /**
     * Test if userId present in the database is given has present
     */
    @Test
    fun isUserStoredInDatabasePresent() {
        val database = MockDatabase()
        val test = database.isUserInDatabase(userIdTest).get()
        assertEquals(test, true)
    }

    /**
     * Test if a userId not present in the database is given has not present
     */
    @Test
    fun isUserStoredInDatabaseNotPresent() {
        val database = MockDatabase()
        val test = database.isUserInDatabase("ex").get()
        assertEquals(test, false)
    }

    /**
     * Test if we obtain the correct username with a given userId present on the database
     */
    @Test
    fun getUsernameFromUserIdPresent() {
        val database = MockDatabase()
        val username = database.getUsername(userIdTest).get()
        assertEquals(username, usernameTest)
    }

    /**
     * Test if we obtain an null username with a given userId not present on the database
     */
    @Test
    fun getUsernameFromUserIdNotPresent() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) { database.getUsername("testinvalid").get() }
    }

    /**
     * Test if we obtain the correct userId with a given username present on the database
     */
    @Test
    fun getUserIdFromUsernamePresent() {
        val database = MockDatabase()
        val userId = database.getUserIdFromUsername(usernameTest).get()
        assertEquals(userId, userIdTest)
    }

    /**
     * Test if we obtain a null userId with a given username not present on the database
     */
    @Test
    fun getUserIdFromUsernameNotPresent() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) { database.getUserIdFromUsername("test").get() }
    }

    /**
     * Test if a given username present on the database is return as unavailable
     */
    @Test
    fun isUsernameAvailablePresent() {
        val database = MockDatabase()
        val availability = database.isUsernameAvailable(usernameTest).get()
        assertEquals(availability, false)
    }

    /**
     * Test if a given username not present on the database is return as available
     */
    @Test
    fun isUsernameAvailableNotPresent() {
        val database = MockDatabase()
        val availability = database.isUsernameAvailable("test").get()
        assertEquals(availability, true)
    }

    /**
     * Test if a given available username is correctly updated in the database
     */
    @Test
    fun updateUsernameAvailable() {
        val database = MockDatabase()
        database.setUsername(userIdTest, "test").get()
        assertEquals(database.unameToUid.contains(usernameTest), false)
        assertEquals(database.unameToUid["test"], userIdTest)
        assertEquals(database.users[userIdTest]!!.username, "test")
    }

    @Test
    fun setUserDataSetsUserData() {
        val newUserData = UserData(
            userId = "foobar",
            surname = "Michel",
            firstname = "Machel",
            email = "mochel@mochel.mochel",
            username = "Muchel",
            goals = UserGoals(
                distance = 12.0,
                activityTime = 15.0,
                paths = 14,
            ),
        )
        val database = MockDatabase()
        database.createUser(newUserData.userId!!, UserData(username = newUserData.username)).get()
        database.setUserData(newUserData.userId!!, newUserData).get()

        val userData = database.getUserData(newUserData.userId!!).get()

        assertEquals(newUserData.email, userData.email)
        assertEquals(newUserData.username, userData.username)
        assertEquals(newUserData.surname, userData.surname)
        assertEquals(newUserData.firstname, userData.firstname)
        assertEquals(newUserData.goals?.paths, userData.goals?.paths)
        assertEquals(newUserData.goals?.distance, userData.goals?.distance)
        assertEquals(newUserData.goals?.activityTime, userData.goals?.activityTime)
    }

    @Test
    fun createUserCreatesUser() {
        val newUserData = UserData(
            userId = "foobar",
            surname = "Michel",
            firstname = "Machel",
            email = "mochel@mochel.mochel",
            username = "Muchel",
            goals = UserGoals(
                distance = 12.0,
                activityTime = 15.0,
                paths = 14,
            ),
        )
        val database = MockDatabase()
        database.createUser(newUserData.userId!!, newUserData).get()

        val userData = database.getUserData(newUserData.userId!!).get()

        assertEquals(newUserData.email, userData.email)
        assertEquals(newUserData.username, userData.username)
        assertEquals(newUserData.surname, userData.surname)
        assertEquals(newUserData.firstname, userData.firstname)
        assertEquals(newUserData.goals?.paths, userData.goals?.paths)
        assertEquals(newUserData.goals?.distance, userData.goals?.distance)
        assertEquals(newUserData.goals?.activityTime, userData.goals?.activityTime)
    }

    /**
     * Test if a given unavailable username is not set in the database
     */
    @Test
    fun updateUsernameNotAvailable() {
        val database = MockDatabase()

        assertThrows(Throwable::class.java) {
            database.setUsername(userIdTest, takenUsername).get()
        }
        assertEquals(userIdTest, database.unameToUid[usernameTest])
        assertEquals(usernameTest, database.users[userIdTest]?.username)
        assertNotNull(database.unameToUid[takenUsername])
    }

    /**
     * Test that setting an available username is correctly set
     */
    @Test
    fun setUsernameAvailable() {
        val database = MockDatabase()
        database.setUsername(userIdTest, "albert").get()
        assertEquals(database.users[userIdTest]?.username, "albert")
        assertEquals(database.unameToUid["albert"], userIdTest)
    }

    /**
     * Test that setting an unavailable username is not set into the database
     */
    @Test
    fun setUsernameNotAvailable() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setUsername(userIdTest, takenUsername).get()
        }
    }

    /**
     * Test if the user account with a given userId is correctly given
     */
    @Test
    fun getUserAccountCorrectly() {
        val database = MockDatabase()
        val user = database.getUserData(userIdTest).get()
        assertEquals(user.userId, userIdTest)
        assertEquals(user.username, usernameTest)
        assertEquals(user.email, userAuthTest.getEmail())
        assertEquals(user.firstname, firstnameTest)
        assertEquals(user.surname, surnameTest)
        assertEquals(user.birthDate, dateOfBirthTest)
        assertEquals(user.goals?.distance ?: 0.0, distanceGoalTest, 0.001)
        assertEquals(user.goals?.activityTime?.toDouble() ?: 0.0, activityTimeGoalTest, 0.001)
        assertEquals(user.goals?.paths ?: 0, nbOfPathsGoalTest.toLong())
    }

    /**
     * Test if the user account legged in the app is correctly given
     */
    @Test
    fun getLoggedUserAccountCorrectly() {
        val database = MockDatabase()
        database.users[userIdTest] = UserData(
            username = usernameTest,
            userId = userIdTest,
            email = userAuthTest.getEmail(),
            firstname = firstnameTest,
            surname = surnameTest,
            birthDate = dateOfBirthTest,
            goals = UserGoals(
                distance = distanceGoalTest,
                activityTime = activityTimeGoalTest,
                paths = nbOfPathsGoalTest.toLong(),
            ),
        )
        val user = database.getUserData(userIdTest).get()
        assertEquals(user.userId, userIdTest)
        assertEquals(user.username, usernameTest)
        assertEquals(user.email, userAuthTest.getEmail())
        assertEquals(user.firstname, firstnameTest)
        assertEquals(user.surname, surnameTest)
        assertEquals(user.birthDate, dateOfBirthTest)
        assertEquals(user.goals?.distance ?: 0.0, distanceGoalTest, 0.001)
        assertEquals((user.goals?.activityTime ?: 0).toDouble(), activityTimeGoalTest, 0.001)
        assertEquals((user.goals?.paths ?: 0).toInt(), nbOfPathsGoalTest)
    }

    /**
     * Test if setting an invalid distance goal throw an error
     */
    @Test
    fun setDistanceGoalInvalid() {
        val database = MockDatabase()
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.setGoals(userIdTest, UserGoals(distance = -1.00)).get()
        }
        assertEquals(
            database.users[userIdTest]?.goals?.distance?.toInt(),
            distanceGoalTest.toInt(),
        )
    }

    /**
     * Test if the distance goal is correctly set
     */
    @Test
    fun setDistanceGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(distance = 13.0)).get()
        assertEquals(
            database.users[userIdTest]?.goals?.distance?.toInt(),
            13,
        )
    }

    /**
     * Test if setting an invalid activity time goal throw an error
     */
    @Test
    fun setActivityTimeGoalInvalid() {
        val database = MockDatabase()
        val exception = Assert.assertThrows(Throwable::class.java) {
            database.setGoals(userIdTest, UserGoals(activityTime = -1.0)).get()
        }
        assertEquals(
            database.users[userIdTest]?.goals?.activityTime?.toInt(),
            activityTimeGoalTest.toInt(),
        )
    }

    /**
     * Test if the activity time goal is correctly set
     */
    @Test
    fun setActivityTimeGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(activityTime = 45.0)).get()
        assertEquals(
            45,
            database.users[userIdTest]?.goals?.activityTime?.toInt(),
        )
    }

    /**
     * Test if setting an invalid number of paths goal throw an error
     */
    @Test
    fun setNbOfPathsGoalInvalid() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setGoals(userIdTest, UserGoals(paths = -1)).get()
        }
    }

    /**
     * Test if the number of paths goal is correctly set
     */
    @Test
    fun setNbOfPathsGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(paths = 1)).get()
        assertEquals(database.users[userIdTest]?.goals?.paths, 1L)
    }

    /**
     * Test if adding a friend to the friendsList that is not on the database throw an error
     */
    @Test
    fun addInvalidUserToFriendsList() {
        val database = MockDatabase()

        val friendCount = database.users[userIdTest]?.friendList?.size ?: 0
        assertThrows(Throwable::class.java) {
            database.addFriend(userIdTest, "faultId").get()
        }
        assertEquals(
            database.users[userIdTest]?.friendList?.size ?: 0,
            friendCount,
        )
    }

    /**
     * Test if adding a friend to the friendsList is correctly added
     */
    @Test
    fun addValidUserToFriendsList() {
        val database = MockDatabase()
        database.addFriend(userIdTest, mockDatabase.MOCK_USERS[0].userId!!).get()

        assertEquals(
            database.users[userIdTest]?.friendList?.contains(mockDatabase.MOCK_USERS[0].userId!!),
            true,
        )

        assertEquals(
            database.users[mockDatabase.MOCK_USERS[0].userId!!]?.friendList?.contains(userIdTest),
            true,
        )
    }

    /**
     * Test if removing a friend to the friendsList is correctly removed
     */
    @Test
    fun removeValidUserToFriendsList() {
        val database = MockDatabase()

        database.addFriend(userIdTest, database.MOCK_USERS[3].userId!!).get()

        val friendCount = database.users[userIdTest]?.friendList?.size ?: 0

        // test if the user has been correctly added
        // test if the same user has been correctly removed
        database.removeFriend(userIdTest, database.MOCK_USERS[3].userId!!).get()

        assertEquals(
            database.users[userIdTest]?.friendList?.size,
            friendCount - 1,
        )
    }

    /**
     * Test if adding and removing runs from the history works and doesn't change the order (based on startTime)
     */
    @Test
    fun addingAndRemovingRunsWorksAndKeepsOrder() {
        val database = MockDatabase()

        // Add a run with starting after the one in database
        val newRun1StartTime = 10 + 1e7.toLong()
        val newRun1 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun1StartTime,
            newRun1StartTime + 2e6.toLong(),
        )
        val expectedHistory = arrayListOf(newRun1).also { it.addAll(database.users[userIdTest]!!.runs!!) }.also { it.reverse() }

        database.addRunToHistory(userIdTest, newRun1).get()

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )

        // Add a run with starting time before the one in database
        val newRun2StartTime = 10 - 1e7.toLong()
        val newRun2 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun2StartTime,
            newRun2StartTime + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun2).get()

        expectedHistory.add(0, newRun2)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )

        // Remove original run
        database.removeRunFromHistory(userIdTest, newRun1)

        expectedHistory.remove(newRun1)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )
    }

    /**
     * Test if adding a run with a startTime equal to an already stored run replaces the run
     * This behavior is the one of the Firebase
     * TODO does not work
     */
    /*@Test
    fun addingNewRunWithSameStartingTimeReplacesOldRun() {
        val database = MockDatabase()
        val newRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            10,
            10 + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun)

        val expectedHistory = listOf(newRun)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )
    }*/

    /**
     * Test if removing a run which does not exist does nothing, as expected
     */
    @Test
    fun removingRunWithNonExistingStartTimeDoesNothing() {
        val database = MockDatabase()
        val nonExistingStartingTime = 10 + 1e7.toLong()
        val nonExistingRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            nonExistingStartingTime,
            nonExistingStartingTime + 2e6.toLong(),
        )
        database.removeRunFromHistory(userIdTest, nonExistingRun)

        assertEquals(
            runHistoryTest.size,
            database.users[userIdTest]?.runs?.size,
        )
    }

    /**
     * Test if adding a dailyGoal in the database is correctly made
     */
    @Test
    fun addDailyGoalCorrect() {
        val database = MockDatabase()
        database.addDailyGoal(
            userIdTest,
            DailyGoal(
                25.0,
                30.0,
                2,
                20.0,
                120.0,
                1,
                LocalDate.of(2010, 1, 1),
            ),
        ).get()

        // control the dailyGoal List
        val dailyGoals =
            database.users[userIdTest]!!.dailyGoals!!
        assertEquals(dailyGoals.size, 2)

        val added = dailyGoals.find { it.date.year == 2010 && it.date.month.value == 1 }!!

        // check the first daily goal
        assertEquals(added.date, LocalDate.of(2010, 1, 1))
        assertEquals(
            added.expectedDistance,
            25.0,
            0.001,
        )
        assertEquals(
            added.expectedTime,
            30.0,
            0.001,
        )
        assertEquals(
            added.expectedPaths,
            2,
        )
        assertEquals(
            added.distance,
            20.0,
            0.001,
        )
        assertEquals(
            added.time,
            120.0,
            0.001,
        )
        assertEquals(
            added.paths,
            1,
        )
    }

    /**
     * Test if the achievements are update correctly
     */
    @Test
    fun updateUserAchievementsCorrect() {
        val database = MockDatabase()
        /*
        database.updateUserAchievements(userIdTest, 10.0, 50.0).get()
        val userAccount = database.users[database.userIdTest]!!
        assertEquals(userAccount.getTotalDistance(), database.totalDistanceTest + 10.0, 0.001)
        assertEquals(
            userAccount.getTotalActivityTime(),
            database.totalActivityTimeTest + 50.0,
            0.001,
        )
        assertEquals(userAccount.getTotalNbOfPaths(), database.totalNbOfPathsTest + 1)
        */
    }

    /**
     * Test if a conversation was correctly created
     */
    @Test
    fun createConversationCorrectly() {
        val database = MockDatabase()
        val convName = "New conversation"
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val welcomeMessage: String =
            targetContext.resources.getString(R.string.welcome_chat_message)
                .format(convName)
        val members: List<String> = listOf(
            database.MOCK_USERS[0].userId!!,
            database.MOCK_USERS[1].userId!!,
            database.MOCK_USERS[2].userId!!,
        )
        val creator = database.MOCK_USERS[0].userId!!

        // past database before executing the function
        val pastChatPreview = database.MOCK_CHAT_PREVIEWS
        val pastChatMembers = database.MOCK_CHAT_MEMBERS
        val pastChatMessages = database.MOCK_CHAT_MESSAGES
        val pastUser0Chat = database.MOCK_USERS[0].chatList
        val userId0 = database.MOCK_USERS[0].userId!!
        val pastUser1Chat = database.MOCK_USERS[1].chatList
        val userId1 = database.MOCK_USERS[1].userId!!
        val pastUser2Chat = database.MOCK_USERS[2].chatList
        val userId2 = database.MOCK_USERS[2].userId!!

        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)

        database.createChatConversation(convName, members, creator, welcomeMessage)

        // test the chat previews
        val newPreview = ChatPreview(
            conversationId = "1",
            title = convName,
            lastMessage = Message.createTextMessage(creator, welcomeMessage, date),
        )
        assertEquals(pastChatPreview + newPreview, database.chatPreviews.values.toList())
        // test the chat members
        val newMembers = ChatMembers(conversationId = "1", membersList = members)
        assertEquals(pastChatMembers + newMembers, database.chatMembers.values.toList())
        // test the chat messages
        val newMessage = ChatMessages(
            conversationId = "1",
            chat = listOf(
                Message(
                    id = date,
                    content = MessageContent.Text(welcomeMessage),
                    senderId = creator,
                    timestamp = date,
                ),
            ),
        )
        assertEquals(pastChatMessages + newMessage, database.chatMessages.values.toList())
        // test the chat list of the different members of the group
        assertEquals(
            if (pastUser0Chat == null) {
                listOf("1")
            } else {
                pastUser0Chat + "1"
            },
            database.users[userId0]!!.chatList,
        )
        assertEquals(
            if (pastUser1Chat == null) {
                listOf("1")
            } else {
                pastUser1Chat + "1"
            },
            database.users[userId1]!!.chatList,
        )
        assertEquals(
            if (pastUser2Chat == null) {
                listOf("1")
            } else {
                pastUser2Chat + "1"
            },
            database.users[userId2]!!.chatList,
        )
    }

    /**
     * Test if the correct chat preview was return
     */
    @Test
    fun getCorrectChatPreview() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        val chatPreview = database.getChatPreview(conversationId)
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], chatPreview.get())
    }

    /**
     * Test if modifying a title of a conversation is correctly made
     */
    @Test
    fun modifyCorrectlyTitleChat() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        database.setChatTitle(conversationId, "new test name")
        val expectedPreview = database.MOCK_CHAT_PREVIEWS[0].copy(title = "new test name")
        assertEquals(expectedPreview, database.chatPreviews[conversationId])
    }

    /**
     * Test if the correct chat members list was return
     */
    @Test
    fun getCorrectChatMembersList() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        val members = database.getChatMemberList(conversationId).get()
        assertEquals(database.MOCK_CHAT_MEMBERS[0].membersList, members)
    }

    /**
     * Test if a member can be added to the memberList of a chat correctly
     */
    @Test
    fun addMemberToChatMemberListCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MEMBERS[0].conversationId!!
        val memberId = database.MOCK_USERS[2].userId!!
        database.addChatMember(memberId, conversationId)
        val expectedMemberList =
            listOf(memberId) + (database.MOCK_CHAT_MEMBERS[0].membersList ?: emptyList())
        // test the member list
        assertEquals(expectedMemberList, database.chatMembers[conversationId]!!.membersList)
        // test the chat list of the user
        assertEquals(
            listOf(conversationId) + (database.MOCK_USERS[2].chatList ?: emptyList()),
            database.users[memberId]?.chatList,
        )
    }

    /**
     * Test if a member can be removed to the memberList of a chat correctly
     */
    @Test
    fun removeMemberToChatMemberListCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MEMBERS[0].conversationId!!
        val removeUser = database.MOCK_USERS[1].userId!!
        // check the user is present before delete it
        assertEquals(
            true,
            database.chatMembers[conversationId]?.membersList?.contains(removeUser) ?: false,
        )
        // check the member list of the chat
        database.removeChatMember(database.MOCK_USERS[1].userId!!, conversationId)
        val expectedMemberList = (database.MOCK_CHAT_MEMBERS[0].membersList ?: emptyList()).stream()
            .filter { it != removeUser }.toList()
        assertEquals(expectedMemberList, database.chatMembers[conversationId]!!.membersList)
        // test the chat list of the user
        assertEquals(
            (database.MOCK_USERS[1].chatList ?: emptyList()).stream()
                .filter { it != conversationId }.toList(),
            database.users[removeUser]!!.chatList,
        )
    }

    /**
     * Test that the correct list is return for a given conversation.
     */
    @Test
    fun getChatMessagesCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val messages = database.getChatMessages(conversationId).get()
        assertEquals(database.MOCK_CHAT_MESSAGES[0].chat, messages)
    }

    /**
     * Test that a text message is correctly added to a conversation
     */
    @Test
    fun addChatTextMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val messageSent = Message.createTextMessage(senderId, "Message Sent!", date)
        database.addChatMessage(conversationId, messageSent)
        // check the chat messages clist
        assertEquals(
            listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()),
            database.chatMessages[conversationId]!!.chat,
        )
        // check the chat preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that a run message is correctly added to a conversation
     */
    @Test
    fun addChatRunMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val messageSent = Message.createRunPathMessage(
            senderId,
            Run(
                startTime = 10,
                endTime = 20,
                path = Path(),
            ),
            date,
        )
        database.addChatMessage(conversationId, messageSent)
        // check the chat messages clist
        assertEquals(
            listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()),
            database.chatMessages[conversationId]!!.chat,
        )
        // check the chat preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that a message not in the preview is correctly deleted
     */
    @Test
    fun removeChatMessageNotPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.get(1).timestamp
        database.removeChatMessage(conversationId, timestamp)
        // check the messages list
        assertEquals((database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()).stream().filter { it.timestamp != timestamp }.toList(), database.chatMessages[conversationId]!!.chat)
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], database.chatPreviews[conversationId])
    }

    /**
     * Test that a message in the conversation preview is correctly deleted
     */
    @Test
    fun removeChatMessageInPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.get(0).timestamp
        database.removeChatMessage(conversationId, timestamp)
        // check the messages list
        assertEquals((database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()).stream().filter { it.timestamp != timestamp }.toList(), database.chatMessages[conversationId]!!.chat)
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = database.MOCK_CHAT_PREVIEWS[0].lastMessage!!.copy(content = MessageContent.Text(database.DELETE_MESSAGE_STR))), database.chatPreviews[conversationId])
    }

    /**
     * Test that modify a given message in a given conversation is correctly made(present in the preview)
     */
    @Test
    fun modifyChatTextMessageInPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.get(0).timestamp
        val newMessage = "edited message"
        database.modifyChatTextMessage(conversationId, timestamp, newMessage)
        // check the messages list
        assertEquals((database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()).stream().map { if (it.timestamp == timestamp) it.copy(content = MessageContent.Text(newMessage)) else it }.toList(), database.chatMessages[conversationId]!!.chat)
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = database.MOCK_CHAT_PREVIEWS[0].lastMessage!!.copy(content = MessageContent.Text(newMessage))), database.chatPreviews[conversationId])
    }

    /**
     * Test that modify a given message in a given conversation is correctly made( not present in the preview)
     */
    @Test
    fun modifyChatTextMessageNotPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.get(1).timestamp
        val newMessage = "edited message"
        database.modifyChatTextMessage(conversationId, timestamp, newMessage)
        // check the messages list
        assertEquals((database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()).stream().map { if (it.timestamp == timestamp) it.copy(content = MessageContent.Text(newMessage)) else it }.toList(), database.chatMessages[conversationId]!!.chat)
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], database.chatPreviews[conversationId])
    }
}

/**
 * Get current date and time in epoch seconds
 * @return current dte and time in epoch seconds
 */
fun getCurrentDateTimeInEpochSeconds(): Long {
    return LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
}
