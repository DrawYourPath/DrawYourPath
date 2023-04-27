package com.epfl.drawyourpath.database

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.epfl.Utils.drawyourpath.Utils
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class MockDatabase : Database() {

    val mockUser = UserData(
        userId = MockAuth.MOCK_USER.getUid(),
        birthDate = 220,
        goals = UserGoals(
            3,
            10.0,
            20,
        ),
        email = MockAuth.MOCK_USER.getEmail(),
        username = "MOCK_USER",
        surname = "testsurnamemock",
        firstname = "testfirstnamemock",
        picture = "1234567890",
        runs = listOf(
            /*Run(
                startTime = 10,
                endTime = 20,
                path = Path()
            )*/
        ),
        dailyGoals = listOf(
            DailyGoal(
                paths = 10,
                distance = 10.0,
                expectedTime = 10.0,
                expectedPaths = 10,
                date = LocalDate.now(),
                expectedDistance = 10.0,
                time = 10.0,
            ),
        ),
        friendList = listOf("0", "1"),
        chatList = listOf("0"),
    )

    val MOCK_USERS = listOf<UserData>(
        UserData(
            userId = "0",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20,
            ),
            email = "test@test.test",
            username = "testusername",
            surname = "testsurname",
            firstname = "testfirstname",
            picture = "1234567890",
            runs = listOf(
                Run(
                    startTime = 10,
                    endTime = 20,
                    path = Path(),
                ),
            ),
            dailyGoals = listOf(
                DailyGoal(
                    paths = 10,
                    distance = 10.0,
                    expectedTime = 10.0,
                    expectedPaths = 10,
                    date = LocalDate.now(),
                    expectedDistance = 10.0,
                    time = 10.0,
                ),
            ),
        ),
        UserData(
            userId = "1",
            birthDate = 220,
            goals = UserGoals(
                3,
                10.0,
                20,
            ),
            email = "test2@test.test",
            username = "testusername2",
            surname = "testsurname2",
            firstname = "testfirstname2",
            picture = "1234567890",
            runs = listOf(
                Run(
                    startTime = 10,
                    endTime = 20,
                    path = Path(),
                ),
            ),
            dailyGoals = listOf(
                DailyGoal(
                    paths = 10,
                    distance = 10.0,
                    expectedTime = 10.0,
                    expectedPaths = 10,
                    date = LocalDate.now(),
                    expectedDistance = 10.0,
                    time = 10.0,
                ),
            ),
            chatList = listOf("0"),
        ),
        UserData(
            userId = "10",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20,
            ),
            email = "test@test.test",
            username = "hugo",
            surname = "testsurname",
            firstname = "testfirstname",
            picture = "1234567890",
            runs = listOf(
                Run(
                    startTime = 10,
                    endTime = 20,
                    path = Path(),
                ),
            ),
            dailyGoals = listOf(
                DailyGoal(
                    paths = 10,
                    distance = 10.0,
                    expectedTime = 10.0,
                    expectedPaths = 10,
                    date = LocalDate.now(),
                    expectedDistance = 10.0,
                    time = 10.0,
                ),
            ),
        ),

        UserData(
            userId = "100",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20,
            ),
            email = "test@test.test",
            username = "Hugo852",
            surname = "testsurname",
            firstname = "testfirstname",
            picture = "1234567890",
            runs = listOf(
                Run(
                    startTime = 10,
                    endTime = 20,
                    path = Path(),
                ),
            ),
            dailyGoals = listOf(
                DailyGoal(
                    paths = 10,
                    distance = 10.0,
                    expectedTime = 10.0,
                    expectedPaths = 10,
                    date = LocalDate.now(),
                    expectedDistance = 10.0,
                    time = 10.0,
                ),
            ),
        ),
        mockUser,
    )

    var MOCK_CHAT_PREVIEWS = listOf<ChatPreview>(
        ChatPreview(
            conversationId = "0",
            title = "New Conversation",
            lastMessage = "Hello",
            lastDate = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC),
            lastSenderId = MOCK_USERS[1].userId
        )
    )

    val MOCK_CHAT_MEMBERS = listOf<ChatMembers>(
        ChatMembers(
            conversationId = "0",
            membersList = listOf(mockUser.userId!!, MOCK_USERS[1].userId!!)
        )
    )

    val MOCK_CHAT_MESSAGES = listOf<ChatMessage>(
        ChatMessage(
            conversationId = "0",
            messageList = listOf(
                Message(
                    conversationId = "0",
                    sender = MOCK_USERS[1].userId,
                    date = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC),
                    content = "Hello"
                ),
                Message(
                    conversationId = "0",
                    sender = mockUser.userId,
                    date = LocalDate.of(2000, 2, 20).atTime(10, 0).toEpochSecond(ZoneOffset.UTC),
                    content = "Hi"
                )
            )
        )
    )

    init {
        ilog("Mock database created.")
    }

    private fun ilog(text: String) {
        Log.i("MOCK DB", text)
    }

    private fun <T> userDoesntExist(userId: String? = null): CompletableFuture<T> {
        return Utils.failedFuture(Error("This user doesn't exist $userId"))
    }

    val unameToUid = MOCK_USERS.associate { it.username to it.userId }.toMutableMap()

    val users = MOCK_USERS.associateBy { it.userId }.toMutableMap()

    val chatPreviews = MOCK_CHAT_PREVIEWS.associateBy { it.conversationId }.toMutableMap()
    val chatMembers = MOCK_CHAT_MEMBERS.associateBy { it.conversationId }.toMutableMap()
    val chatMessages = MOCK_CHAT_MESSAGES.associateBy { it.conversationId }.toMutableMap()

    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(unameToUid.containsValue(userId))
    }

    override fun getUsername(userId: String): CompletableFuture<String> {
        if (users.contains(userId)) {
            return CompletableFuture.completedFuture(users[userId]!!.username!!)
        }
        return Utils.failedFuture(Exception("There is no username corresponding to the userId $userId\""))
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        if (unameToUid.contains(username)) {
            return CompletableFuture.completedFuture(unameToUid[username]!!)
        }
        return Utils.failedFuture(Exception("There is no userId corresponding to the username $username"))
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(!unameToUid.contains(userName))
    }

    override fun setUsername(userId: String, username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // Checks for availability.
        if (unameToUid.contains(username)) {
            future.completeExceptionally(Error("Username already taken"))
            return future
        }

        ilog("Settings username $username for user $userId")

        // Create a new mapping to the new username.
        unameToUid[username] = userId
        unameToUid.remove(users[userId]?.username)
        users[userId] = users[userId]?.copy(username = username) ?: UserData(username = username)

        future.complete(Unit)

        return future
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        if (users.contains(userId)) {
            return Utils.failedFuture(Error("This user already exists"))
        }

        users[userId] = userData

        return CompletableFuture.completedFuture(Unit)
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            goals = UserGoals(
                distance = userData.goals?.distance ?: current.goals?.distance,
                paths = userData.goals?.paths ?: current.goals?.paths,
                activityTime = userData.goals?.activityTime ?: current.goals?.activityTime,
            ),
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun getUserData(userId: String): CompletableFuture<UserData> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        return CompletableFuture.completedFuture(users[userId]!!)
    }

    override fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            Log.e("DYP", users.toString())
            return userDoesntExist(userId)
        }

        if (goals.distance != null && goals.distance <= 0.0) {
            return Utils.failedFuture(Exception("Distance must be greater than 0."))
        }

        if (goals.paths != null && goals.paths <= 0) {
            return Utils.failedFuture(Exception("Path must be greater than 0."))
        }

        if (goals.activityTime != null && goals.activityTime <= 0) {
            return Utils.failedFuture(Exception("Activity Time must be greater than 0."))
        }

        return setUserData(userId, UserData(goals = goals))
    }

    override fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        return CompletableFuture.completedFuture(Unit)
    }

    private fun addFriendToUser(user: String, target: String) {
        val current = users[user]!!
        users[user] = current.copy(
            friendList = listOf(target) + (current.friendList ?: emptyList()),
        )
    }

    override fun addFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        if (!users.contains(userId) || !users.contains(targetFriend)) {
            return userDoesntExist()
        }
        addFriendToUser(userId, targetFriend)
        addFriendToUser(targetFriend, userId)

        return CompletableFuture.completedFuture(Unit)
    }

    private fun removeFriendForUser(user: String, target: String) {
        val current = users[user]
        if (current != null) {
            users[user] = current.copy(
                friendList = (current.friendList ?: emptyList()).stream().filter { it != target }
                    .toList(),
            )
        }
    }

    override fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        removeFriendForUser(userId, targetFriend)
        removeFriendForUser(targetFriend, userId)

        return CompletableFuture.completedFuture(Unit)
    }

    override fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            runs = ((current.runs ?: emptyList()) + run).sortedBy {
                it.getStartTime()
            },
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            runs = (current.runs ?: emptyList()).stream()
                .filter { it.getStartTime() != run.getStartTime() }.toList(),
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            dailyGoals = (current.dailyGoals ?: emptyList()) + dailyGoal,
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun updateUserAchievements(
        userId: String,
        distanceDrawing: Double,
        activityTimeDrawing: Double,
    ): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }

    override fun createChatConversation(
        name: String,
        membersList: List<String>,
        creatorId: String
    ): CompletableFuture<Unit> {
        //create the id of the new conversation
        val conversationId: String = (chatPreviews.size).toString()

        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val welcomeMessage: String =
            targetContext.resources.getString(R.string.welcome_chat_message)
                .format(name)
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        return initChatPreview(
            conversationId,
            chatPreview = ChatPreview(
                title = name,
                lastMessage = welcomeMessage,
                lastSenderId = creatorId,
                lastDate = date
            )
        )
            .thenApply {
                initChatMembers(conversationId, membersList)
            }.thenApply {
                initChatMessages(
                    conversationId,
                    Message(sender = creatorId, content = welcomeMessage, date = date)
                )
            }.thenApply {
                updateMembersProfileWithNewChat(conversationId, membersList)
            }
    }

    /**
     * Helper function to initiate the chat preview of a given conversation
     * @param conversationId id of the new conversation
     * @param chatPreview object that contains the data of this preview
     * @return a future that indicate if the chat preview was correctly created
     */
    private fun initChatPreview(
        conversationId: String,
        chatPreview: ChatPreview
    ): CompletableFuture<Unit> {
        chatPreviews[conversationId] = ChatPreview(
            conversationId = conversationId,
            title = chatPreview.title,
            lastMessage = chatPreview.lastMessage,
            lastSenderId = chatPreview.lastSenderId,
            lastDate = chatPreview.lastDate
        )

        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Helper function to initiate the chat members of a given conversation
     * @param conversationId id of the new conversation
     * @param membersList list of members of the conversation
     * @return a future that indicate if the chat members have been correctly initiated.
     */
    private fun initChatMembers(
        conversationId: String,
        membersList: List<String>
    ): CompletableFuture<Unit> {
        chatMembers[conversationId] =
            ChatMembers(conversationId = conversationId, membersList = membersList)

        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Helper function to initiate the chat messages of a given conversation
     * @param conversationId id of the new conversation
     * @param firstMessage first message of the conversation
     */
    private fun initChatMessages(
        conversationId: String,
        firstMessage: Message
    ): CompletableFuture<Unit> {
        chatMessages[conversationId] = ChatMessage(
            conversationId = conversationId, messageList = listOf(
                Message(
                    conversationId = conversationId,
                    content = firstMessage.content,
                    sender = firstMessage.sender,
                    date = firstMessage.date
                )
            )
        )
        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Helper function to add the conversation id of new conversation to the profile of each member.
     * @param conversationId id of the new conversation
     * @param membersList list of all the members of the conversation
     * @return a future that indicate if every member profile have been correctly updated
     */
    private fun updateMembersProfileWithNewChat(
        conversationId: String,
        membersList: List<String>
    ): CompletableFuture<Unit> {
        for (memberId in membersList) {
            val current = users[memberId]!!
            users[memberId] = current.copy(
                chatList = (current.chatList ?: emptyList()) + conversationId,
            )
        }

        return CompletableFuture.completedFuture(Unit)
    }
}
