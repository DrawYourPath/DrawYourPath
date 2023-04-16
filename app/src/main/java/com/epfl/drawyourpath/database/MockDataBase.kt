package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.userProfile.UserModel
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MockDataBase : Database() {
    //clean database
    //link to username to userId
    var usernameToUserId: HashMap<String, String> = HashMap()

    //user account
    val userIdToUserAccount: HashMap<String, UserModel> = HashMap()
    var userIdToUsername: HashMap<String, String> = HashMap() //1=userId 2=username

    val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    val userAuthTest: User = MockAuth.MOCK_USER
    val userMailTest: String = userAuthTest.getEmail()
    val usernameTest: String = "albert"
    val distanceGoalTest: Double = 10.0
    val activityTimeGoalTest: Double = 60.0
    val nbOfPathsGoalTest: Int = 5
    val firstnameTest = "Hugo"
    val surnameTest = "Hof"
    val takenUsername = "nathan"
    val dateOfBirthTest = LocalDate.of(2000, 2, 20)
    val friendsListTest: List<String>
    val userModelTest: UserModel
    val userIdFriend1: String = "idFriend1"
    val userIdFriend2: String = "idFriend2"
    val friend1: UserModel
    val friend2: UserModel

    init {
        //init the friendsList of the current user
        friendsListTest = listOf<String>(userIdFriend1)

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
            distanceGoalTest,
            activityTimeGoalTest,
            nbOfPathsGoalTest,
            null,
            friendsListTest,
            this
        )
        friend1 = UserModel(userIdFriend1, "friend1@mail.com", "friend1","firstnameFriendOne", "surnameFriendOne", LocalDate.of(2000,1,1),
            10.0,60.0,2, null, emptyList(),this)
        friend2 = UserModel(userIdFriend2, "friend2@mail.com", "friend2","firstnameFriendTwo", "surnameFriendTwo", LocalDate.of(2000,1,1),
            10.0,60.0,2, null, emptyList(),this)
        //add the different user to the database
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

    override fun updateUsername(username: String): CompletableFuture<Boolean> {
        return isUsernameAvailable(username).thenApply {
            if (it) {
                //remove the past username has taken
                val pastUsername = userIdToUsername.get(userIdTest)
                usernameToUserId.remove(pastUsername)
                //add the new username link to the userId
                usernameToUserId.put(username, userIdTest)
                //edit the userProfile
                userIdToUsername.put(userIdTest, username)
            }
            it
        }
    }

    override fun setUsername(username: String): CompletableFuture<Boolean> {
        return isUsernameAvailable(username).thenApply {
            if (it) {
                usernameToUserId.put(username, userIdTest)
                userIdToUsername.put(userIdTest, username)
            }
            it
        }
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean> {
        userIdToUserAccount.put(userIdTest, userModel)
        return CompletableFuture.completedFuture(true)
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        return CompletableFuture.completedFuture(userIdToUserAccount.get(userId))
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        return getUserAccount(userIdTest)
    }

    override fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        if (distanceGoal <= 0.0) {
            future.completeExceptionally(Error("The distance goal can't be less or equal than 0."))
            return future
        }
        val updatedUser = UserModel(
            userAuthTest, usernameTest, firstnameTest, surnameTest, dateOfBirthTest, distanceGoal,
            activityTimeGoalTest, nbOfPathsGoalTest, this
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
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
            distanceGoalTest,
            activityTimeGoal,
            nbOfPathsGoalTest,
            this
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun setNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
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
            distanceGoalTest,
            activityTimeGoalTest,
            nbOfPathsGoal,
            this
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        val updatedUser = UserModel(
            userIdTest, userAuthTest.getEmail(), usernameTest, firstnameTest, surnameTest,
            dateOfBirthTest, distanceGoalTest, activityTimeGoalTest, nbOfPathsGoalTest, photo, friendsListTest, this
        )
        userIdToUserAccount.put(userIdTest, updatedUser)
        return CompletableFuture.completedFuture(true)
    }

    override fun addUserToFriendsList(userId: String): CompletableFuture<Unit> {
        return isUserStoredInDatabase(userId).thenApply {
            if(!it){
                throw Exception("The user with $userId is not present on the database.")
            }else{
                //add the user to the the friendList of the current user
                addUserIdToFriendList(userIdTest, userId).thenApply {
                    //add the currentUser to the friend list of the user with userId
                    addUserIdToFriendList(userId, userIdTest)
                }
            }
        }
    }

    override fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit> {
        //remove the userId from the friendlist of the current user
        return removeUserIdToFriendList(userIdTest, userId).thenApply {
            //remove the current userId to the friendlist of the user with userId
            removeUserIdToFriendList(userId, userIdTest)
        }
    }
    /**
     * Helper function to add a userId "friendUserId" to the friendList of a a user with userId "currentUserId"
     * @param currentUserId userId that belong the friendlist
     * @param friendUserId userId that we want to add from the friendlist
     * @return a future that indicate if the userId has been correctly added to the friendlist
     */
    private fun addUserIdToFriendList(currentUserId: String, friendUserId: String): CompletableFuture<Unit>{
        val future = CompletableFuture<Unit>()

        //create the field for the new friend
        val newFriend = HashMap<String, Boolean>()
        newFriend.put(friendUserId, true)
        //updated the friendlist in the database
        getUserAccount(currentUserId).thenAccept {user->
            val newFriendList = user.getFriendList().toMutableList()
            newFriendList.add(friendUserId)
            val newUser = UserModel(user.getUserId(), user.getEmailAddress(), user.getUsername(), user.getFirstname()
            ,user.getSurname(), user.getDateOfBirth(), user.getDistanceGoal(), user.getActivityTime(), user.getNumberOfPathsGoal(),
            user.getProfilePhoto(), newFriendList, this)
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
    private fun removeUserIdToFriendList(currentUserId: String, removeUserId: String): CompletableFuture<Unit>{
        val future = CompletableFuture<Unit>()

        //obtain the previous friendList
        getUserAccount(currentUserId).thenAccept {user ->
            val previousFriendList = user.getFriendList()
            val newFriendList = previousFriendList.filter { it != removeUserId }
            val newUser = UserModel(user.getUserId(), user.getEmailAddress(), user.getUsername(), user.getFirstname()
                ,user.getSurname(), user.getDateOfBirth(), user.getDistanceGoal(), user.getActivityTime(), user.getNumberOfPathsGoal(),
                user.getProfilePhoto(), newFriendList, this)
            userIdToUserAccount.put(currentUserId, newUser)
            future.complete(Unit)
        }
        return future
    }
}

