package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.milestone.MilestoneEnum
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class MockDatabase : Database() {
    companion object {
        val mockUser = UserData(
            userId = MockAuth.MOCK_USER.getUid(),
            birthDate = 220,
            goals = UserGoals(
                3,
                10.0,
                20.0,
            ),
            email = MockAuth.MOCK_USER.getEmail(),
            username = "MOCK_USER",
            surname = "testsurnamemock",
            firstname = "testfirstnamemock",
            picture = "1234567890",
            runs = listOf(
                Run(
                    startTime = 10,
                    endTime = 20,
                    duration = 10,
                    path = Path(listOf(listOf(LatLng(46.51854301997813, 6.56237289547834)))),
                    predictedShape = "Circle",
                    similarityScore = 0.9,
                ),
                Run(
                    startTime = 20,
                    endTime = 50,
                    duration = 10,
                    path = Path(listOf(listOf(LatLng(46.51854301997813, 6.56237289547834)))),
                    predictedShape = "square",
                    similarityScore = 1.65321,
                ),
            ),
            dailyGoals = listOf(
                DailyGoal(
                    paths = 10,
                    distance = 10.0,
                    expectedTime = 10.0,
                    expectedPaths = 10,
                    date = LocalDate.of(2020, 1, 1),
                    expectedDistance = 10.0,
                    time = 10.0,
                ),
                DailyGoal(
                    paths = 1,
                    distance = 3.0,
                    expectedTime = 20.0,
                    expectedPaths = 10,
                    date = LocalDate.of(2022, 1, 1),
                    expectedDistance = 10.0,
                    time = 2.0,
                ),
            ),
            friendList = listOf("0", "1"),
            trophies = listOf(
                Trophy(tournamentId = "0", tournamentName = "tournament0", tournamentDescription = "description0", date = LocalDate.of(2000, 2, 20), ranking = 1),
            ),
            milestones = listOf(
                MilestoneData(MilestoneEnum.HUNDRED_KILOMETERS, LocalDate.of(2000, 2, 20)),
                MilestoneData(MilestoneEnum.THE_FIRST_KILOMETER, LocalDate.of(2001, 2, 20)),
                MilestoneData(MilestoneEnum.THE_FIRST_DAY, LocalDate.of(2002, 2, 20)),
            ),
            tournaments = listOf("0", "personalTournament"),
            chatList = listOf("0"),
        )
    }

    // Please keep this list with more than 3 users to have enough data for tournaments.
    val MOCK_USERS = listOf(
        UserData(
            userId = "0",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20.0,
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
                    duration = 10,
                    predictedShape = "Cat",
                    similarityScore = -0.8,
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
            tournaments = listOf("0", "1"),
            friendList = listOf(MockAuth.MOCK_USER.getUid()),
        ),
        UserData(
            userId = "1",
            birthDate = 220,
            goals = UserGoals(
                3,
                10.0,
                20.0,
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
                    duration = 10,
                    predictedShape = "Dog",
                    similarityScore = 0.7,
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
            tournaments = listOf("0", "1", "2"),
            chatList = listOf("0"),
        ),
        UserData(
            userId = "10",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20.0,
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
                    duration = 10,
                ),
                Run(
                    startTime = 30,
                    endTime = 40,
                    path = Path(),
                    duration = 10,
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
            tournaments = listOf("0"),
        ),

        UserData(
            userId = "100",
            birthDate = 120,
            goals = UserGoals(
                10,
                10.0,
                20.0,
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
                    duration = 10,
                    predictedShape = "testShape",
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
            tournaments = listOf("0"),
        ),
        mockUser,
    )

    val mockPost = TournamentPost(
        postId = "mockPostID",
        tournamentId = "0",
        tournamentName = "mockTournament0",
        userId = mockUser.userId!!,
        run = mockUser.runs!![0],
        usersVotes = hashMapOf(
            MOCK_USERS[0].userId!! to 1,
            MOCK_USERS[1].userId!! to -1,
        ),
    )

    val MOCK_POSTS = listOf(
        mockPost,
        TournamentPost(
            postId = "mockPostID2",
            tournamentId = "0",
            tournamentName = "mockTournament0",
            userId = MOCK_USERS[0].userId!!,
            run = MOCK_USERS[0].runs!![0],
        ),
    )

    val mockTournament = MutableLiveData(
        Tournament(
            id = "0",
            name = "mockTournament0",
            description = "Mock tournament number 0",
            creatorId = MockAuth.MOCK_USER.getUid(),
            startDate = LocalDateTime.now().minusDays(3L),
            endDate = LocalDateTime.now().plusDays(4L),
            participants = MOCK_USERS.map { it.userId!! },
            // The next args are useless for now
            posts = listOf(mockPost),
            visibility = Tournament.Visibility.PUBLIC,
        ),
    )

    val MOCK_TOURNAMENTS_ID = MutableLiveData(
        listOf(
            "0",
            "1",
            "2",
            "personalTournament",
        ),
    )

    val MOCK_TOURNAMENTS = listOf(
        mockTournament,
        MutableLiveData(
            Tournament(
                id = "1",
                name = "mockTournament1",
                description = "Mock tournament number 1",
                creatorId = MOCK_USERS[0].userId!!,
                startDate = LocalDateTime.now().plusDays(1L),
                endDate = LocalDateTime.now().plusDays(2L),
                participants = listOf(MOCK_USERS[0].userId!!, MOCK_USERS[1].userId!!),
                posts = listOf(),
                visibility = Tournament.Visibility.PUBLIC,
            ),
        ),
        MutableLiveData(
            Tournament(
                id = "2",
                name = "mockTournament2",
                description = "Mock tournament number 2",
                creatorId = MOCK_USERS[1].userId!!,
                startDate = LocalDateTime.now().plusDays(2L),
                endDate = LocalDateTime.now().plusDays(3L),
                participants = listOf(MOCK_USERS[1].userId!!),
                posts = listOf(),
                visibility = Tournament.Visibility.PUBLIC,
            ),
        ),
        MutableLiveData(
            Tournament(
                id = "personalTournament",
                name = "personalTournamentName",
                description = "Mock tournament number personalTournament",
                creatorId = mockUser.userId!!,
                startDate = LocalDateTime.now().minusDays(2L),
                endDate = LocalDateTime.now().minusDays(1L),
                participants = listOf(mockUser.userId!!, MOCK_USERS[0].userId!!),
                posts = listOf(
                    TournamentPost(
                        postId = "winPost",
                        tournamentId = "personalTournament",
                        tournamentName = "personalTournamentName",
                        userId = mockUser.userId!!,
                        run = mockUser.runs!!.get(0)!!,
                        date = LocalDateTime.now().minusDays(2L),
                        usersVotes = mutableMapOf(MOCK_USERS[0].userId!! to 1),
                    ),
                ),
                visibility = Tournament.Visibility.PUBLIC,
            ),
        ),
    )

    var mockUniqueId = 1234567

    var MOCK_CHAT_PREVIEWS = listOf(
        ChatPreview(
            conversationId = "0",
            title = "New Conversation",
            lastMessage = Message(id = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC), senderId = MOCK_USERS[1].userId!!, content = MessageContent.Text("Hello"), timestamp = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC)),
        ),
    )

    val MOCK_CHAT_MEMBERS = listOf(
        ChatMembers(
            conversationId = "0",
            membersList = listOf(mockUser.userId!!, MOCK_USERS[1].userId!!),
        ),
    )

    val MOCK_CHAT_MESSAGES = listOf(
        ChatMessages(
            conversationId = "0",
            chat = MutableLiveData(
                listOf(
                    Message(
                        id = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC),
                        senderId = MOCK_USERS[1].userId!!,
                        timestamp = LocalDate.of(2000, 2, 20).atTime(11, 0).toEpochSecond(ZoneOffset.UTC),
                        content = MessageContent.Text("Hello"),
                    ),
                    Message(
                        id = LocalDate.of(2000, 2, 20).atTime(10, 0).toEpochSecond(ZoneOffset.UTC),
                        senderId = mockUser.userId!!,
                        timestamp = LocalDate.of(2000, 2, 20).atTime(10, 0).toEpochSecond(ZoneOffset.UTC),
                        content = MessageContent.Text("Hi"),
                    ),
                ),
            ),
        ),
    )
    val DELETE_MESSAGE_STR = "This message was deleted !"

    init {
        ilog("Mock database created.")
    }

    private fun ilog(text: String) {
        Log.i("MOCK DB", text)
    }

    private fun <T> userDoesntExist(userId: String? = null): CompletableFuture<T> {
        return Utils.failedFuture(Error("This user doesn't exist $userId"))
    }

    private fun <T> tournamentDoesntExist(tournamentId: String): CompletableFuture<T> {
        return Utils.failedFuture(Error("This tournament doesn't exist $tournamentId"))
    }

    private fun <T> postDoesntExist(tournamentId: String, postId: String): CompletableFuture<T> {
        return Utils.failedFuture(Error("This post doesn't exist $postId (in tournament $tournamentId)"))
    }

    val unameToUid = MOCK_USERS.associate { it.username to it.userId }.toMutableMap()

    val users = MOCK_USERS.associateBy { it.userId }.toMutableMap()

    val tournaments = MOCK_TOURNAMENTS.associateBy { it.value!!.id }.toMutableMap()

    val chatPreviews = MOCK_CHAT_PREVIEWS.associateBy { it.conversationId }.toMutableMap()
    val chatMembers = MOCK_CHAT_MEMBERS.associateBy { it.conversationId }.toMutableMap()
    val chatMessages = MOCK_CHAT_MESSAGES.associateBy { it.conversationId }.toMutableMap()

    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(unameToUid.containsValue(userId))
    }

    override fun isTournamentInDatabase(tournamentId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(tournaments.containsKey(tournamentId))
    }

    override fun isPostInDatabase(
        tournamentId: String,
        postId: String,
    ): CompletableFuture<Boolean> {
        val postExists = tournaments.keys.contains(tournamentId) &&
            tournaments[tournamentId]!!.value!!.posts.any { it.postId == postId }
        return CompletableFuture.completedFuture(postExists)
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

        ilog("Settings username $username for user $userId.")

        // Create a new mapping to the new username.
        unameToUid[username] = userId
        unameToUid.remove(users[userId]?.username)
        users[userId] = users[userId]?.copy(username = username) ?: UserData(username = username)

        future.complete(Unit)

        return future
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        val current = users[userId] ?: UserData()

        users[userId] = UserData(
            userId = userId,
            email = userData.email ?: current.email,
            username = userData.username ?: current.username,
            firstname = userData.firstname ?: current.firstname,
            picture = userData.picture ?: current.picture,
            surname = userData.surname ?: current.surname,
            friendList = userData.friendList ?: current.friendList,
            runs = userData.runs ?: current.runs,
            birthDate = userData.birthDate ?: current.birthDate,
            dailyGoals = userData.dailyGoals ?: current.dailyGoals,
            goals = UserGoals(
                distance = userData.goals?.distance ?: current.goals?.distance,
                paths = userData.goals?.paths ?: current.goals?.paths,
                activityTime = userData.goals?.activityTime ?: current.goals?.activityTime,
            ),
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        // The same as creating a user, except we can't edit uid and uname.
        createUser(userId, userData.copy(userId = null, username = null))

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
        // convert the bitmap to a byte array
        return setUserData(userId, UserData(picture = Utils.encodePhotoToString(photo)))
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

        val currentUser = users[userId]!!
        val currentRuns = currentUser.runs?.filter { it.getStartTime() != run.getStartTime() }

        users[userId] = currentUser.copy(
            runs = ((currentRuns ?: emptyList()) + run).sortedBy {
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

    override fun addTrophy(userId: String, trophy: Trophy): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            trophies = (current.trophies ?: emptyList()) + trophy,
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun addMilestone(
        userId: String,
        milestone: MilestoneEnum,
        date: LocalDate,
    ): CompletableFuture<Unit> {
        if (!users.contains(userId)) {
            return userDoesntExist()
        }

        val current = users[userId]!!

        users[userId] = current.copy(
            milestones = (current.milestones ?: emptyList()) + MilestoneData(milestone, date),
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun getAllTournamentsId(): LiveData<List<String>> {
        return MOCK_TOURNAMENTS_ID
    }

    override fun getTournamentUniqueId(): String {
        return mockUniqueId++.toString()
    }

    override fun addTournament(tournament: Tournament): CompletableFuture<Unit> {
        // add the tournament to the list of tournament ids
        MOCK_TOURNAMENTS_ID.postValue((MOCK_TOURNAMENTS_ID.value ?: emptyList()) + tournament.id)
        // Replaces if id already exists, which would happen with Firebase but should never happen as we generate unique ids.
        tournaments[tournament.id] = MutableLiveData(tournament)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun removeTournament(tournamentId: String): CompletableFuture<Unit> {
        // check if tournament exists, if not do nothing (no fail future)
        if (!tournaments.contains(tournamentId)) {
            return CompletableFuture.completedFuture(Unit)
        }
        // 1. remove the tournament from the list of tournaments of all participants
        tournaments[tournamentId]!!.value!!.participants.forEach { userId ->
            if (users.contains(userId)) {
                val currentUser = users[userId]!!
                users[userId] = currentUser.copy(
                    tournaments = users[userId]!!.tournaments?.filter { it != tournamentId },
                )
            }
        }
        // 2. remove the tournament from the tournaments file
        tournaments.remove(tournamentId)

        // 3. remove tournament from the tournament id list
        MOCK_TOURNAMENTS_ID.postValue((MOCK_TOURNAMENTS_ID.value ?: emptyList()).stream().toList().filter { it != tournamentId })

        return CompletableFuture.completedFuture(Unit)
    }

    override fun addUserToTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        // check that the userId and tournamentId exist
        if (!users.contains(userId)) {
            return userDoesntExist(userId)
        }
        if (!tournaments.contains(tournamentId)) {
            return tournamentDoesntExist(tournamentId)
        }

        // add tournament to user
        val currentUser = users[userId]!!
        users[userId] = currentUser.copy(
            tournaments = (
                (currentUser.tournaments ?: emptyList()).filter {
                    it != tournamentId
                } + tournamentId
                ),
        )
        // add user to tournament
        val currentTournament = tournaments[tournamentId]!!.value
        tournaments[tournamentId]!!.postValue(
            currentTournament!!.copy(
                participants = (
                    currentTournament.participants.filter {
                        it != userId
                    } + userId
                    ),
            ),
        )

        return CompletableFuture.completedFuture(Unit)
    }

    override fun removeUserFromTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        // check that the userId and tournamentId exist and remove them if it's the case
        if (users.contains(userId)) {
            val currentUser = users[userId]!!
            users[userId] = currentUser.copy(
                tournaments = currentUser.tournaments?.filter { it != tournamentId },
            )
        }
        if (tournaments.contains(tournamentId)) {
            val currentTournament = tournaments[tournamentId]!!.value!!
            tournaments[tournamentId]!!.postValue(
                currentTournament.copy(
                    participants = currentTournament.participants.filter { it != userId },
                ),
            )
        }
        return CompletableFuture.completedFuture(Unit)
    }

    override fun getTournament(tournamentId: String): LiveData<Tournament> {
        if (!tournaments.contains(tournamentId)) {
            throw Error("This tournament doesn't exist $tournamentId")
        }
        return tournaments[tournamentId]!!
    }

    override fun getTournamentPosts(tournamentId: String): LiveData<List<TournamentPost>> {
        if (!tournaments.contains(tournamentId)) {
            throw Error("This tournament doesn't exist $tournamentId")
        }
        val postsLiveData = MutableLiveData<List<TournamentPost>>()
        Handler(Looper.getMainLooper()).post {
            tournaments[tournamentId]!!.observeForever {
                postsLiveData.postValue(it.posts)
            }
        }
        return postsLiveData
    }

    override fun getTournamentParticipantsId(tournamentId: String): CompletableFuture<List<String>> {
        if (!tournaments.contains(tournamentId)) {
            return tournamentDoesntExist(tournamentId)
        }
        return CompletableFuture.completedFuture(tournaments[tournamentId]!!.value!!.participants)
    }

    override fun getTournamentInfo(tournamentId: String): LiveData<Tournament> {
        if (!tournaments.contains(tournamentId)) {
            throw Error("This tournament doesn't exist $tournamentId")
        }
        val infoLiveData = MutableLiveData<Tournament>()
        Handler(Looper.getMainLooper()).post {
            tournaments[tournamentId]!!.observeForever {
                infoLiveData.postValue(it.copy(participants = emptyList(), posts = emptyList()))
            }
        }
        return infoLiveData
    }

    override fun getPostUniqueId(): String {
        return mockUniqueId++.toString()
    }

    override fun addPostToTournament(
        tournamentId: String,
        post: TournamentPost,
    ): CompletableFuture<Unit> {
        if (!tournaments.contains(tournamentId)) {
            return tournamentDoesntExist(tournamentId)
        }
        val oldTournament = tournaments[tournamentId]!!.value
        val oldPosts = oldTournament!!.posts.filter { it.postId != post.postId }
        val newTournament = oldTournament.copy(posts = oldPosts.plus(post))
        tournaments[tournamentId]!!.postValue(newTournament)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun voteOnPost(
        userId: String,
        tournamentId: String,
        postId: String,
        vote: Int,
    ): CompletableFuture<Unit> {
        // check that the userId and postId exist
        if (!users.contains(userId)) {
            return userDoesntExist(userId)
        }
        if (!isPostInDatabase(tournamentId, postId).get()) {
            return postDoesntExist(tournamentId, postId)
        }

        val oldTournament = tournaments[tournamentId]!!.value!!
        val oldPosts = oldTournament.posts
        val oldPost = oldPosts.first { it.postId == postId }
        val oldPostIndex = oldPosts.indexOf(oldPost)
        val newUsersVotes = oldPost.getUsersVotes().plus(userId to vote).toMutableMap()
        val newPost = oldPost.copy(usersVotes = newUsersVotes)
        val newPosts = oldPosts.toMutableList()
        newPosts.set(oldPostIndex, newPost)

        tournaments[tournamentId]!!.postValue(oldTournament.copy(posts = newPosts))

        return CompletableFuture.completedFuture(Unit)
    }

    override fun createChatConversation(
        name: String,
        membersList: List<String>,
        creatorId: String,
        welcomeMessage: String,
    ): CompletableFuture<String> {
        // create the id of the new conversation
        val conversationId: String = (chatPreviews.size).toString()

        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        return initChatPreview(
            conversationId,
            chatPreview = ChatPreview(
                title = name,
                lastMessage = Message(id = date, senderId = creatorId, content = MessageContent.Text(welcomeMessage), timestamp = date),
            ),
        )
            .thenApply {
                initChatMembers(conversationId, membersList)
            }.thenApply {
                initChatMessages(
                    conversationId,
                    Message(id = date, senderId = creatorId, content = MessageContent.Text(welcomeMessage), timestamp = date),
                )
            }.thenApply {
                updateMembersProfileWithNewChat(conversationId, membersList)
                conversationId
            }
    }

    override fun getChatPreview(conversationId: String): LiveData<ChatPreview> {
        return MutableLiveData(
            ChatPreview(
                conversationId = conversationId,
                title = chatPreviews[conversationId]!!.title,
                lastMessage = chatPreviews[conversationId]!!.lastMessage,
            ),
        )
    }

    override fun getChatList(userId: String): LiveData<List<String>> {
        return MutableLiveData(
            users[userId]!!.chatList,
        )
    }

    override fun getFriendsList(userId: String): LiveData<List<String>> {
        return MutableLiveData(
            users[userId]?.friendList ?: emptyList(),
        )
    }

    override fun setChatTitle(conversationId: String, newTitle: String): CompletableFuture<Unit> {
        val current = chatPreviews[conversationId]
        chatPreviews[conversationId] = current!!.copy(title = newTitle)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun getChatMemberList(conversationId: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(chatMembers[conversationId]!!.membersList)
    }

    override fun addChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        // add the new member to the member chat list
        val current = chatMembers[conversationId]!!
        chatMembers[conversationId] = current.copy(membersList = listOf(userId) + (current.membersList ?: emptyList()))
        // add the conversationId to the chat list of the user with userId
        val currentUser = users[userId]!!
        users[userId] = currentUser.copy(chatList = listOf(conversationId) + (currentUser.chatList ?: emptyList()))
        return CompletableFuture.completedFuture(Unit)
    }

    override fun removeChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        // remove member to the members chat list
        val current = chatMembers[conversationId]!!
        chatMembers[conversationId] = current.copy(membersList = (current.membersList ?: emptyList()).stream().filter { it != userId }.toList())
        // remove the conversationId to the chat list of the user with userId
        val currentUser = users[userId]!!
        users[userId] = currentUser.copy(chatList = (currentUser.chatList ?: emptyList()).stream().filter { it != conversationId }.toList())
        return CompletableFuture.completedFuture(Unit)
    }

    override fun getChatMessages(conversationId: String): LiveData<List<Message>> {
        return chatMessages[conversationId]!!.chat!!
    }

    override fun addChatMessage(conversationId: String, message: Message): CompletableFuture<Unit> {
        // update the messages list
        val current = chatMessages[conversationId]!!
        chatMessages[conversationId]!!.chat!!.postValue(listOf(message) + (current.chat!!.value ?: emptyList()))
        // update the preview
        val currentPreview = chatPreviews[conversationId]!!
        chatPreviews[conversationId] = currentPreview.copy(lastMessage = message)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun removeChatMessage(
        conversationId: String,
        messageId: Long,
    ): CompletableFuture<Unit> {
        // update the messages list
        val current = chatMessages[conversationId]!!
        chatMessages[conversationId]!!.chat!!.postValue((current.chat?.value ?: emptyList()).stream().filter { it.timestamp != messageId }.toList())
        // update the preview if needed
        if (chatPreviews[conversationId]!!.lastMessage!!.timestamp == messageId) {
            val currentPreview = chatPreviews[conversationId]!!
            val currentMessage = currentPreview.lastMessage!!
            chatPreviews[conversationId] = currentPreview.copy(lastMessage = currentMessage.copy(content = MessageContent.Text(DELETE_MESSAGE_STR)))
        }
        return CompletableFuture.completedFuture(Unit)
    }

    override fun modifyChatTextMessage(
        conversationId: String,
        messageId: Long,
        message: String,
    ): CompletableFuture<Unit> {
        // update the messages list
        val current = chatMessages[conversationId]!!
        chatMessages[conversationId]!!.chat!!.postValue(
            (current.chat?.value ?: emptyList()).stream().map {
                if (it.timestamp == messageId) it.copy(content = MessageContent.Text(message)) else it
            }.toList(),
        )
        // update the preview if needed
        if (chatPreviews[conversationId]!!.lastMessage!!.timestamp == messageId) {
            val currentPreview = chatPreviews[conversationId]!!
            val currentMessage = currentPreview.lastMessage!!
            chatPreviews[conversationId] = currentPreview.copy(lastMessage = currentMessage.copy(content = MessageContent.Text(message)))
        }
        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Helper function to initiate the chat preview of a given conversation
     * @param conversationId id of the new conversation
     * @param chatPreview object that contains the data of this conversation preview
     * @return a future that indicate if the chat preview was correctly created
     */
    private fun initChatPreview(
        conversationId: String,
        chatPreview: ChatPreview,
    ): CompletableFuture<Unit> {
        chatPreviews[conversationId] = ChatPreview(
            conversationId = conversationId,
            title = chatPreview.title,
            lastMessage = chatPreview.lastMessage,
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
        membersList: List<String>,
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
        firstMessage: Message,
    ): CompletableFuture<Unit> {
        chatMessages[conversationId] = ChatMessages(
            conversationId = conversationId,
            chat = MutableLiveData(
                listOf(
                    Message(
                        id = firstMessage.id,
                        content = firstMessage.content,
                        senderId = firstMessage.senderId,
                        timestamp = firstMessage.timestamp,
                    ),
                ),
            ),
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
        membersList: List<String>,
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
