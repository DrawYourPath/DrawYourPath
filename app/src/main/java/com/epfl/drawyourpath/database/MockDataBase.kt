package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.challenge.DailyGoal
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    // clean database
    // link to username to userId
    var usernameToUserId: HashMap<String, String> = HashMap()

    // user account
    val userIdToUserAccount: HashMap<String, UserModel> = HashMap()
    var userIdToUsername: HashMap<String, String> = HashMap() // 1=userId 2=username

    val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    val userAuthTest: User = MockAuth.MOCK_USER
    val userMailTest: String = userAuthTest.getEmail()
    val usernameTest: String = "albert"
    val currentDistanceGoalTest: Double = 10.0
    val currentActivityTimeGoalTest: Double = 60.0
    val currentNbOfPathsGoalTest: Int = 5
    val firstnameTest = "Hugo"
    val surnameTest = "Hof"
    val takenUsername = "nathan"
    val dateOfBirthTest = LocalDate.of(2000, 2, 20)
    val friendsListTest: List<String>
    val userModelTest: UserModel

    // for the dailyGoals tests
    val dailyGoalListTest: List<DailyGoal> = listOf(
        DailyGoal(
            10.0,
            60.0,
            5,
            9.0,
            50.0,
            4,
            LocalDate.of(2000, 2, 20),
        ),
    )

    // for the achievements part test
    val totalDistanceTest: Double = 10.0
    val totalActivityTimeTest: Double = 60.0
    val totalNbOfPathsTest: Int = 5

    // for the friendlist tests
    val userIdFriend1: String = "idFriend1"
    val userIdFriend2: String = "idFriend2"
    val friend1: UserModel
    val friend2: UserModel

    // for the history tests
    val runsHistoryTest: List<Run>
    val runTest: Run
    val runTestStartTime: Long = 1651673000000

    init {
        // init the friendsList of the current user
        friendsListTest = listOf(userIdFriend1)

        // init the runs history of the current user
        runTest = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0))),
            runTestStartTime,
            runTestStartTime + 1e6.toLong(),
        )
        runsHistoryTest = listOf(runTest)

        usernameToUserId.put(usernameTest, userIdTest)
        usernameToUserId.put(takenUsername, "exId")
        usernameToUserId.put("friend1", userIdFriend1)
        usernameToUserId.put("friend2", userIdFriend2)

        userIdToUsername.put(userIdTest, usernameTest)
        userIdToUsername.put(userIdFriend1, "friend1")
        userIdToUsername.put(userIdFriend2, "friend2")

        userModelTest = UserModel(
            userIdTest,
            userMailTest,
            usernameTest,
            firstnameTest,
            surnameTest,
            dateOfBirthTest,
            currentDistanceGoalTest,
            currentActivityTimeGoalTest,
            currentNbOfPathsGoalTest,
            null,
            friendsListTest,
            runsHistoryTest,
            this,
            dailyGoalListTest,
            totalDistanceTest,
            totalActivityTimeTest,
            totalNbOfPathsTest,
        )
        friend1 = UserModel(
            userIdFriend1,
            "friend1@mail.com",
            "friend1",
            "firstnameFriendOne",
            "surnameFriendOne",
            LocalDate.of(2000, 1, 1),
            10.0,
            60.0,
            2,
            null,
            emptyList(),
            emptyList(),
            this,
        )
        friend2 = UserModel(
            userIdFriend2,
            "friend2@mail.com",
            "friend2",
            "firstnameFriendTwo",
            "surnameFriendTwo",
            LocalDate.of(2000, 1, 1),
            10.0,
            60.0,
            2,
            null,
            emptyList(),
            emptyList(),
            this,
        )
        // add the different user to the database
        userIdToUserAccount.put(userIdTest, userModelTest)
        userIdToUserAccount.put(userIdFriend1, friend1)
        userIdToUserAccount.put(userIdFriend2, friend2)
    }

    override fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(userIdToUsername.contains(userId))
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(userIdToUsername.get(userId))
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        return CompletableFuture.completedFuture(usernameToUserId.get(username))
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(!usernameToUserId.contains(userName))
    }

    override fun updateUsername(username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        isUsernameAvailable(username).thenApply {
            if (it) {
                // remove the past username has taken
                val pastUsername = userIdToUsername.get(userIdTest)
                usernameToUserId.remove(pastUsername)
                // add the new username link to the userId
                usernameToUserId.put(username, userIdTest)
                // edit the userProfile
                userIdToUsername.put(userIdTest, username)
                future.complete(Unit)
            } else {
                future.completeExceptionally(java.lang.Error("The username is not available !"))
            }
        }
        return future
    }

    override fun setUsername(username: String): CompletableFuture<Unit> {
        return isUsernameAvailable(username).thenApply {
            if (it) {
                usernameToUserId.put(username, userIdTest)
                userIdToUsername.put(userIdTest, username)
            }
        }
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Unit> {
        userIdToUserAccount.put(userIdTest, userModel)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        return CompletableFuture.completedFuture(userIdToUserAccount.get(userId))
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        return getUserAccount(userIdTest)
    }

    override fun setCurrentDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        if (distanceGoal <= 0.0) {
            future.completeExceptionally(Error("The distance goal can't be less or equal than 0."))
            return future
        }
        val updatedUser = UserModel(
            userAuthTest, usernameTest, firstnameTest, surnameTest, dateOfBirthTest, distanceGoal,
            currentActivityTimeGoalTest, currentNbOfPathsGoalTest, this,
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun setCurrentActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        if (activityTimeGoal <= 0.0) {
            future.completeExceptionally(Error("The activity time goal can't be less or equal than 0."))
            return future
        }
        val updatedUser = UserModel(
            userAuthTest,
            usernameTest,
            firstnameTest,
            surnameTest,
            dateOfBirthTest,
            currentDistanceGoalTest,
            activityTimeGoal,
            currentNbOfPathsGoalTest,
            this,
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun setCurrentNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        if (nbOfPathsGoal <= 0) {
            future.completeExceptionally(Error("The number of paths goal can't be less or equal than 0."))
            return future
        }
        val updatedUser = UserModel(
            userAuthTest,
            usernameTest,
            firstnameTest,
            surnameTest,
            dateOfBirthTest,
            currentDistanceGoalTest,
            currentActivityTimeGoalTest,
            nbOfPathsGoal,
            this,
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        val updatedUser = UserModel(
            userIdTest,
            userAuthTest.getEmail(),
            usernameTest,
            firstnameTest,
            surnameTest,
            dateOfBirthTest,
            currentDistanceGoalTest,
            currentActivityTimeGoalTest,
            currentNbOfPathsGoalTest,
            photo,
            friendsListTest,
            runsHistoryTest,
            this,
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(Unit)
    }

    override fun addUserToFriendsList(userId: String): CompletableFuture<Unit> {
        return isUserStoredInDatabase(userId).thenApply {
            if (!it) {
                throw Exception("The user with $userId is not present on the database.")
            } else {
                // add the user to the the friendList of the current user
                addUserIdToFriendList(userIdTest, userId).thenApply {
                    // add the currentUser to the friend list of the user with userId
                    addUserIdToFriendList(userId, userIdTest)
                }
            }
        }
    }

    override fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit> {
        // remove the userId from the friendlist of the current user
        return removeUserIdToFriendList(userIdTest, userId).thenApply {
            // remove the current userId to the friendlist of the user with userId
            removeUserIdToFriendList(userId, userIdTest)
        }
    }

    override fun addDailyGoal(dailyGoal: DailyGoal): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        getLoggedUserAccount().thenAccept { user ->
            val newDailyGoalList = user.getDailyGoalList().toMutableList()
            newDailyGoalList.add(dailyGoal)
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                user.getFriendList(),
                user.getRunsHistory(),
                this,
                newDailyGoalList,
            )
            userIdToUserAccount.put(userIdTest, newUser)
            future.complete(Unit)
        }
        return future
    }

    override fun updateUserAchievements(
        distanceDrawing: Double,
        activityTimeDrawing: Double,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        getLoggedUserAccount().thenAccept { user ->
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                user.getFriendList(),
                user.getRunsHistory(),
                this,
                user.getDailyGoalList(),
                user.getTotalDistance() + distanceDrawing,
                user.getTotalActivityTime() + activityTimeDrawing,
                user.getTotalNbOfPaths() + 1,
            )
            userIdToUserAccount.put(userIdTest, newUser)
            future.complete(Unit)
        }
        return future
    }

    /**
     * Helper function to add a userId "friendUserId" to the friendList of a a user with userId "currentUserId"
     * @param currentUserId userId that belong the friendlist
     * @param friendUserId userId that we want to add from the friendlist
     * @return a future that indicate if the userId has been correctly added to the friendlist
     */
    private fun addUserIdToFriendList(
        currentUserId: String,
        friendUserId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // update the friendList in the database
        getUserAccount(currentUserId).thenAccept { user ->
            val newFriendList = user.getFriendList().toMutableList()
            newFriendList.add(friendUserId)
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                newFriendList,
                user.getRunsHistory(),
                this,
            )
            userIdToUserAccount.put(currentUserId, newUser)
            future.complete(Unit)
        }

        return future
    }

    /**
     * Helper function to remove a userId "removeUserId" to the friendList of a a user with userId "currentUserId"
     * @param currentUserId userId that belong the friendlist
     * @param removeUserId userId that we want to remove from the friendlist
     * @return a future that indicate if the userId has been correctly added to the friendlist
     */
    private fun removeUserIdToFriendList(
        currentUserId: String,
        removeUserId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // obtain the previous friendList
        getUserAccount(currentUserId).thenAccept { user ->
            val previousFriendList = user.getFriendList()
            val newFriendList = previousFriendList.filter { it != removeUserId }
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                newFriendList,
                user.getRunsHistory(),
                this,
            )
            userIdToUserAccount.put(currentUserId, newUser)
            future.complete(Unit)
        }
        return future
    }

    override fun addRunToHistory(run: Run): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // update the history in the database
        getUserAccount(userIdTest).thenAccept { user ->
            // filter and sort on startTime to have the same behavior as Firebase
            val newHistory =
                user.getRunsHistory().filter { it.getStartTime() != run.getStartTime() }
                    .toMutableList()
            newHistory.add(run)
            newHistory.sortBy { it.getStartTime() }
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                user.getFriendList(),
                newHistory,
                this,
            )
            userIdToUserAccount.put(userIdTest, newUser)
            future.complete(Unit)
        }
        return future
    }

    override fun removeRunFromHistory(run: Run): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // obtain the previous runs history and remove filter out the run
        getUserAccount(userIdTest).thenAccept { user ->
            val previousHistory = user.getRunsHistory()
            val newHistory = previousHistory.filter { it.getStartTime() != run.getStartTime() }
            val newUser = UserModel(
                user.getUserId(),
                user.getEmailAddress(),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getDateOfBirth(),
                user.getCurrentDistanceGoal(),
                user.getCurrentActivityTime(),
                user.getCurrentNumberOfPathsGoal(),
                user.getProfilePhoto(),
                user.getFriendList(),
                newHistory,
                this,
            )
            userIdToUserAccount.put(userIdTest, newUser)
            future.complete(Unit)
        }
        return future
    }
}
