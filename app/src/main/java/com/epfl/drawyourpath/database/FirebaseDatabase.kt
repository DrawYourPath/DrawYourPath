package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.util.Log
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.CompletableFuture

class FirebaseKeys {
    companion object {
        // Database root entries
        const val USERS_ROOT = "users"
        const val USERNAMES_ROOT = "usernameToUid"
        const val TOURNAMENTS_ROOT = "tournaments"
        const val CHATS_ROOT = "chats"
        const val CHATS_MEMBERS_ROOT = "chatsMembers"
        const val CHATS_MESSAGES_ROOT = "chatsMessages"

        // User keys top level
        const val PROFILE = "profile"
        const val GOALS = "goals"
        const val RUN_HISTORY = "runs"
        const val FRIENDS = "friends"
        const val DAILY_GOALS = "dailyGoals"
        const val USER_TOURNAMENTS = "tournaments"
        const val USER_CHATS = "chats"

        // User profile keys sublevel
        const val USERNAME = "username"
        const val FIRSTNAME = "firstname"
        const val SURNAME = "surname"
        const val BIRTHDATE = "birth"
        const val EMAIL = "email"
        const val PICTURE = "picture"

        // User goal keys sublevel
        const val GOAL_PATH = "paths"
        const val GOAL_DISTANCE = "distance"
        const val GOAL_TIME = "time"

        // Goals history list sublevels
        const val GOAL_HISTORY_EXPECTED_DISTANCE = "expectedDistance"
        const val GOAL_HISTORY_EXPECTED_PATHS = "expectedPaths"
        const val GOAL_HISTORY_EXPECTED_TIME = "expectedTime"
        const val GOAL_HISTORY_DISTANCE = "distance"
        const val GOAL_HISTORY_PATHS = "paths"
        const val GOAL_HISTORY_TIME = "time"

        // Tournaments keys
        const val TOURNAMENT_PARTICIPANTS_IDS = "participants"

        // Chats keys top level
        const val CHAT_TITLE = "title"
        const val CHAT_LAST_MESSAGE = "lastMessage"

        // Chats messages keys to level
        const val CHAT_MESSAGE_SENDER = "sender"
        const val CHAT_MESSAGE_CONTENT_TEXT = "text"
        const val CHAT_MESSAGE_CONTENT_IMAGE = "image"
        const val CHAT_MESSAGE_CONTENT_RUN = "run"
    }
}

/**
 * The Firebase contains files:
 * -usernameToUserId: that link the username to a unique userId
 * -users: that contains users based on the UserModel defined by their userId
 * -tournaments: that contains the different tournaments
 */
class FirebaseDatabase(reference: DatabaseReference = Firebase.database.reference) : Database() {
    val database: DatabaseReference = reference

    private fun userRoot(userId: String): DatabaseReference {
        return database.child(FirebaseKeys.USERS_ROOT).child(userId)
    }

    private fun userProfile(userId: String): DatabaseReference {
        return userRoot(userId).child(FirebaseKeys.PROFILE)
    }

    private fun nameMappingRoot(): DatabaseReference {
        return database.child(FirebaseKeys.USERNAMES_ROOT)
    }

    private fun nameMapping(username: String): DatabaseReference {
        return nameMappingRoot().child(username)
    }

    private fun tournamentsRoot(): DatabaseReference {
        return database.child(FirebaseKeys.TOURNAMENTS_ROOT)
    }

    /**
     * Helper function to return the file in the chats root
     * @return the file in the chats root
     */
    private fun chatsRoot(): DatabaseReference {
        return database.child(FirebaseKeys.CHATS_ROOT)
    }

    /**
     * Helper function to return the file in the members chats root
     * @return the file in the members chats root
     */
    private fun chatsMembersRoot(): DatabaseReference {
        return database.child(FirebaseKeys.CHATS_MEMBERS_ROOT)
    }

    /**
     * Helper function to return the file in the messages chats root
     * @return the file in the messages chats root
     */
    private fun chatsMessagesRoot(): DatabaseReference {
        return database.child(FirebaseKeys.CHATS_MESSAGES_ROOT)
    }

    /**
     * Helper function to return the file of a conversation with his id
     * @param conversationId id of the conversation
     * @return the file corresponding to this id in the chats file root.
     */
    private fun chatPreview(conversationId: String): DatabaseReference {
        return chatsRoot().child(conversationId)
    }

    /**
     * Helper function to return the file containing the members of a given conversation
     * @param conversationId id of the conversation
     * @return the file containing the member of a given conversation
     */
    private fun chatMembers(conversationId: String): DatabaseReference {
        return chatsMembersRoot().child(conversationId)
    }

    /**
     * Helper function to return the file containing the messages of a given conversation
     * @param conversationId id of the conversation
     * @return the file containing the messages of a given conversation.
     */
    private fun chatMessages(conversationId: String): DatabaseReference {
        return chatsMessagesRoot().child(conversationId)
    }

    /**
     * Helper function to return the file containing a message of a given conversation
     * @param conversationId id of the conversation
     * @param timestamp of the message in milliseconds
     * @return the file containing the message of a given conversation(with the corresponding message id).
     */
    private fun message(conversationId: String, timestamp: Long): DatabaseReference {
        return chatMessages(conversationId).child(timestamp.toString())
    }

    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        userProfile(userId).child(FirebaseKeys.USERNAME).get()
            .addOnSuccessListener {
                future.complete(it.value != null)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun isTournamentInDatabase(tournamentId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        tournamentsRoot().get()
            .addOnSuccessListener {
                future.complete(it.value != null)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun getUsername(userId: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        userProfile(userId).child(FirebaseKeys.USERNAME).get()
            .addOnSuccessListener {
                if (it.value !is String) {
                    future.completeExceptionally(
                        NoSuchFieldException("There is no username corresponding to the userId $userId"),
                    )
                } else {
                    future.complete(it.value as String)
                }
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        Log.i("Database", "Solving uid of $username.")

        nameMapping(username).get()
            .addOnSuccessListener {
                if (it.value !is String) {
                    future.completeExceptionally(NoSuchFieldException("There is no userId corresponding to the username $username"))
                } else {
                    Log.i("Database", "$username solved to uid ${it.value as String}.")
                    future.complete(it.value as String)
                }
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        nameMapping(userName).get().addOnSuccessListener {
            future.complete(it.value == null)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun setUsername(userId: String, username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        ilog("Setting username $username for userid $userId")

        // Checks for availability.
        isUsernameAvailable(username).handle { isAvailable, exc1 ->
            if (!isAvailable || exc1 != null) {
                future.completeExceptionally(Error("Username already taken"))
                return@handle
            }

            // Wanted username is available, we get the old username.
            getUsername(userId).handle { pastUsername, exc2 ->

                // Create a new mapping to the new username.
                nameMapping(username).setValue(userId).addOnSuccessListener {
                    // update the username in the user profile(username)
                    userRoot(userId).child(FirebaseKeys.PROFILE).child(FirebaseKeys.USERNAME)
                        .setValue(username).addOnSuccessListener {
                            // If there is a past username.
                            if (pastUsername != null) {
                                // And remove the old one.
                                nameMapping(pastUsername).removeValue { _, _ ->
                                    future.complete(Unit)
                                }
                            } else {
                                future.complete(Unit)
                            }
                        }.addOnFailureListener { future.completeExceptionally(it) }
                }.addOnFailureListener {
                    future.completeExceptionally(Error("Failed to write new username"))
                }
            }
        }

        return future
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val data = listOf<Pair<String, Any?>>(
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.USERNAME}" to userData.username,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.BIRTHDATE}" to userData.birthDate,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.EMAIL}" to userData.email,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.PICTURE}" to userData.picture,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.SURNAME}" to userData.surname,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.FIRSTNAME}" to userData.firstname,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_DISTANCE}" to userData.goals?.distance,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_PATH}" to userData.goals?.paths,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_TIME}" to userData.goals?.activityTime,
        ).filter { it.second != null }.associate { entry -> entry }

        userRoot(userId).updateChildren(data)
            .addOnSuccessListener {
                Log.i("Database", "Created user $userId (${userData.username}).")

                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        // Setting user data is the same as creating a user, except we don't update the username
        // when the user already exists.
        return createUser(userId, userData.copy(username = null, userId = null))
    }

    override fun getUserData(userId: String): CompletableFuture<UserData> {
        val future = CompletableFuture<UserData>()

        userRoot(userId).get().addOnSuccessListener { data ->
            future.complete(mapToUserData(data, userId))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit> {
        Utils.checkGoals(goals)
        return setUserData(userId, UserData(goals = goals))
    }

    override fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit> {
        // TODO: Use Firebase Storage.
        return setUserData(userId, UserData(picture = Utils.encodePhotoToString(photo)))
    }

    override fun addFriend(
        userId: String,
        targetFriend: String,
    ): CompletableFuture<Unit> {
        val result = CompletableFuture<Unit>()

        isUserInDatabase(targetFriend).thenApply { exists ->
            if (exists) {
                addUserIdToFriendList(userId, targetFriend).thenApply {
                    // add the currentUser to the friend list of the user with userId
                    addUserIdToFriendList(targetFriend, userId)
                    result.complete(Unit)
                }.exceptionally {
                    result.completeExceptionally(it)
                }
            } else {
                result.completeExceptionally(Error("This user doesn't exist."))
            }
        }.exceptionally {
            result.completeExceptionally(it)
        }

        return result
    }

    override fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return removeUserIdToFriendList(userId, targetFriend).thenApply {
            // remove the current userId to the friendlist of the user with userId
            removeUserIdToFriendList(targetFriend, userId)
        }
    }

    override fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit> {
        // create the field for the new path, the key is the start time
        return setData(
            userId,
            hashMapOf(
                "${FirebaseKeys.RUN_HISTORY}/${run.getStartTime()}" to run,
            ),
        )
    }

    override fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        userRoot(userId).child(FirebaseKeys.RUN_HISTORY).child(run.getStartTime().toString())
            .removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // This is how the data is store in the database:
        // dailyGoals = {
        //    date = {
        //        expectedDistance = Value
        //        expectedTime = Value
        //        expectedNbOfPaths = Value
        //        obtainedDistance = Value
        //        obtainedActivityTime = Value
        //        obtainedNbOfPaths = Value
        //    }
        //    date = {.....}
        // }

        // transform daily goal to data(except the date)
        val dailyGoalData = transformDailyGoals(dailyGoal)

        // add the daily goal or update it if it already exist with the new data at this date
        userRoot(userId).child(FirebaseKeys.DAILY_GOALS)
            .child(dailyGoal.date.toEpochDay().toString())
            .updateChildren(dailyGoalData)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun updateUserAchievements(
        userId: String,
        distanceDrawing: Double,
        activityTimeDrawing: Double,
    ): CompletableFuture<Unit> {
        // TODO: Rewrite this function so it can scale as we add achievements.
        //       Current implementation is too restrictive.
        /*
        This is how the achievements are store in the firebase:
        Users{
            userId{
                username: Value
                ....
                achievements{
                    totalDistance: Value
                    totalActivityTime: Value
                    totalNbOfPaths: Value
                }
            }
        }
         */
        /*

        val future = CompletableFuture<Unit>()

        // obtain the current achievements
        getCurrentUserAchievements().thenApply { pastAchievements ->
            val newAchievement = HashMap<String, Any>()
            newAchievement.put(
                totalDistanceFile,
                pastAchievements.get(totalDistanceFile) as Double + distanceDrawing
            )
            newAchievement.put(
                totalActivityTimeFile,
                pastAchievements.get(totalActivityTimeFile) as Double + activityTimeDrawing
            )
            newAchievement.put(
                totalNbOfPathsFile,
                pastAchievements.get(totalNbOfPathsFile) as Int + 1
            )
            val userId = getUserId()
            if (userId == null) {
                future.completeExceptionally(Error("The userId can't be null !"))
            } else {
                userAccountFile(userId).child(achievementsFile).updateChildren(newAchievement)
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }
        }
        return future
        */
        return Utils.failedFuture(Error("Not implemented"))
    }

    override fun getTournamentUID(): String? {
        return database.child(FirebaseKeys.TOURNAMENTS_ROOT).push().key
    }

    override fun addTournament(tournament: Tournament): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // add the tournament to all tournaments
        tournamentsRoot().updateChildren(mapOf(tournament.id to tournament))
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun removeTournament(tournamentId: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        database.child("${FirebaseKeys.TOURNAMENTS_ROOT}/$tournamentId/${FirebaseKeys.TOURNAMENT_PARTICIPANTS_IDS}")
            .get()
            .addOnSuccessListener { data ->
                // get the list of all participants (list of ids)
                val participantsIds = FirebaseDatabaseUtils.getKeys(data)
                // prepare changes in database
                // 1. remove the tournament from the list of tournaments of all participants
                val changes: MutableMap<String, Any?> = participantsIds.associate {
                    "${FirebaseKeys.USERS_ROOT}/$it/${FirebaseKeys.USER_TOURNAMENTS}/$tournamentId" to null
                }.toMutableMap()
                // 2. remove the tournament from the tournaments file
                changes["${FirebaseKeys.TOURNAMENTS_ROOT}/$tournamentId"] = null
                // do the changes
                database.updateChildren(changes).addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun addUserToTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // check that the userId and tournamentId exist
        isUserInDatabase(userId).thenCombine(isTournamentInDatabase(tournamentId)) { userExists, tournamentExists ->
            if (!userExists) {
                future.completeExceptionally(Exception("The user with userId $userId doesn't exist."))
            } else if (!tournamentExists) {
                future.completeExceptionally(Exception("The tournament with tournamentId  $tournamentId doesn't exist."))
            } else {
                // if they exist, do the operation
                val changes: MutableMap<String, Any?> = hashMapOf(
                    "${FirebaseKeys.TOURNAMENTS_ROOT}/$tournamentId/${FirebaseKeys.TOURNAMENT_PARTICIPANTS_IDS}/$userId" to true,
                    "${FirebaseKeys.USERS_ROOT}/$userId/${FirebaseKeys.USER_TOURNAMENTS}/$tournamentId" to true,
                )
                database.updateChildren(changes)
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }
        }
        return future
    }

    override fun removeUserFromTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // this operation requires two deletions
        val changes: MutableMap<String, Any?> = hashMapOf(
            "${FirebaseKeys.TOURNAMENTS_ROOT}/$tournamentId/${FirebaseKeys.TOURNAMENT_PARTICIPANTS_IDS}/$userId" to null,
            "${FirebaseKeys.USERS_ROOT}/$userId/${FirebaseKeys.USER_TOURNAMENTS}/$tournamentId" to null,
        )
        database.updateChildren(changes).addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun createChatConversation(
        name: String,
        membersList: List<String>,
        creatorId: String,
        welcomeMessage: String,
    ): CompletableFuture<String> {
        // create the id of the new conversation
        val pushedPostRef: DatabaseReference = chatsRoot().push()
        val conversationId: String? = pushedPostRef.key

        if (conversationId == null) {
            return Utils.failedFuture(Exception("Error in the generation of the conversation id !"))
        } else {
            val date = Utils.getCurrentDateAsEpoch()
            return initChatPreview(
                conversationId,
                chatPreview = ChatPreview(
                    title = name,
                    lastMessage = Message(
                        id = date,
                        senderId = creatorId,
                        content = MessageContent.Text(welcomeMessage),
                        timestamp = date,
                    ),
                ),
            )
                .thenApply {
                    initChatMembers(conversationId, membersList)
                }.thenApply {
                    initChatMessages(
                        conversationId,
                        Message(
                            id = date,
                            senderId = creatorId,
                            content = MessageContent.Text(welcomeMessage),
                            timestamp = date,
                        ),
                    )
                }.thenApply {
                    updateMembersProfileWithNewChat(conversationId, membersList)
                    conversationId
                }
        }
    }

    override fun getChatPreview(conversationId: String): CompletableFuture<ChatPreview> {
        val future = CompletableFuture<ChatPreview>()
        chatPreview(conversationId).get()
            .addOnSuccessListener { data ->
                val preview = ChatPreview(
                    conversationId = conversationId,
                    title = data.child(FirebaseKeys.CHAT_TITLE).value as String?,
                    lastMessage = FirebaseDatabaseUtils.transformMessage(
                        data.child(FirebaseKeys.CHAT_LAST_MESSAGE).children.toMutableList()[0],
                    ),
                )
                future.complete(preview)
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun setChatTitle(conversationId: String, newTitle: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val data = mapOf(
            FirebaseKeys.CHAT_TITLE to newTitle,
        )
        chatPreview(conversationId).updateChildren(data)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun getChatMemberList(conversationId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        chatMembers(conversationId).get().addOnSuccessListener { data ->
            val members = (data.value as Map<*, *>).mapNotNull { it.key as String? }
            future.complete(members)
        }.addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun addChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // add the new member to the members list
        val newMember = HashMap<String, Any>()
        newMember[userId] = true
        chatMembers(conversationId).updateChildren(newMember).addOnSuccessListener {
            // add the chat to the chat list of the user with userId
            val newChat = mapOf(conversationId to true)
            userProfile(userId).child(FirebaseKeys.USER_CHATS).updateChildren(newChat)
                .addOnSuccessListener { future.complete(Unit) }
                .addOnFailureListener { future.completeExceptionally(it) }
        }.addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun removeChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // remove the member to the member chat list
        chatMembers(conversationId).child(userId).removeValue()
            .addOnSuccessListener {
                // remove the chat to the user with userId chat list
                userProfile(userId).child(FirebaseKeys.USER_CHATS).child(conversationId)
                    .removeValue()
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }.addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun getChatMessages(conversationId: String): CompletableFuture<List<Message>> {
        val future = CompletableFuture<List<Message>>()
        chatMessages(conversationId).get().addOnSuccessListener { data ->
            val listMessage = data.children.map { FirebaseDatabaseUtils.transformMessage(it) }
            future.complete(listMessage)
        }.addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun addChatMessage(conversationId: String, message: Message): CompletableFuture<Unit> {
        // add the message to the list of the messages of the conversation
        when (message.content.javaClass) {
            MessageContent.RunPath::class.java -> return addChatRunMessage(conversationId, message)
            MessageContent.Picture::class.java -> return addChatPictureMessage(
                conversationId,
                message,
            )

            MessageContent.Text::class.java -> return addChatTextMessage(conversationId, message)
        }
        return Utils.failedFuture(Error("No type found for the content of the message !"))
    }

    override fun removeChatMessage(
        conversationId: String,
        messageId: Long,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        chatMessages(conversationId).child(messageId.toString()).removeValue()
            .addOnSuccessListener {
                // check if we must update the preview
                chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE)
                    .child(messageId.toString()).get()
                    .addOnSuccessListener { data ->
                        if (data.value != null) {
                            val lastMessage = mapOf(
                                FirebaseKeys.CHAT_MESSAGE_SENDER to data.child(FirebaseKeys.CHAT_MESSAGE_SENDER).value as String,
                                FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT to "This message was deleted",
                            )
                            chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE)
                                .child(messageId.toString()).updateChildren(lastMessage)
                                .addOnSuccessListener { future.complete(Unit) }
                                .addOnFailureListener { future.completeExceptionally(it) }
                        }
                        future.complete(Unit)
                    }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun modifyChatTextMessage(
        conversationId: String,
        messageId: Long,
        message: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val newMessage = mapOf(
            FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT to message,
        )
        message(conversationId, messageId).updateChildren(newMessage)
            .addOnSuccessListener {
                chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE)
                    .child(messageId.toString()).get().addOnSuccessListener { preview ->
                        if (preview.value != null) {
                            chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE)
                                .child(messageId.toString()).updateChildren(newMessage)
                                .addOnSuccessListener { future.complete(Unit) }
                                .addOnFailureListener { future.completeExceptionally(it) }
                        }
                    }.addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    /**
     * Helper function to update data of the current user account
     * @param data to be updated
     * @return a future to indicated if the data have been correctly updated
     */
    private fun setData(userId: String, data: Map<String, Any?>): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        userRoot(userId).updateChildren(data)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    /**
     * Helper function to convert a data snapshot to a userModel
     * @param data data snapshot to convert
     * @param userId of the user
     * @return ta future that contains the user Model
     */
    private fun mapToUserData(data: DataSnapshot, userId: String): UserData {
        val profile = data.child(FirebaseKeys.PROFILE)
        val goals = data.child(FirebaseKeys.GOALS)

        return UserData(
            userId = userId,
            username = profile.child(FirebaseKeys.USERNAME).value as String?,
            firstname = profile.child(FirebaseKeys.FIRSTNAME).value as String?,
            surname = profile.child(FirebaseKeys.SURNAME).value as String?,
            birthDate = profile.child(FirebaseKeys.BIRTHDATE).value as Long?,
            email = profile.child(FirebaseKeys.EMAIL).value as String?,
            goals = UserGoals(
                paths = (goals.child(FirebaseKeys.GOAL_PATH).value as Number?)?.toLong(),
                distance = (goals.child(FirebaseKeys.GOAL_DISTANCE).value as Number?)?.toDouble(),
                activityTime = (goals.child(FirebaseKeys.GOAL_TIME).value as Number?)?.toDouble(),
            ),
            picture = profile.child(FirebaseKeys.PICTURE).value as String?,
            runs = FirebaseDatabaseUtils.transformRuns(data.child(FirebaseKeys.RUN_HISTORY)),
            dailyGoals = FirebaseDatabaseUtils.transformDailyGoals(data.child(FirebaseKeys.DAILY_GOALS)),
            friendList = FirebaseDatabaseUtils.getKeys(profile.child(FirebaseKeys.FRIENDS)),
            chatList = FirebaseDatabaseUtils.transformChatList(data.child(FirebaseKeys.USER_CHATS)),
        )
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

        // updated the friendlist in the database
        userRoot(currentUserId).child(FirebaseKeys.PROFILE).child(FirebaseKeys.FRIENDS)
            .child(friendUserId)
            .setValue(true)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

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
        userRoot(currentUserId).child(FirebaseKeys.PROFILE).child(FirebaseKeys.FRIENDS).child(removeUserId).removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { err -> future.completeExceptionally(err) }

        return future
    }

    /**
     * Helper function to transform a DailyGoal of a certain date object to a data object
     * @param dailyGoal DailyGoal object that we would like to transform in data object
     */
    private fun transformDailyGoals(dailyGoal: DailyGoal): HashMap<String, Any> {
        return hashMapOf(
            FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE to dailyGoal.expectedDistance,
            FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME to dailyGoal.expectedTime,
            FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS to dailyGoal.expectedPaths,
            FirebaseKeys.GOAL_HISTORY_DISTANCE to dailyGoal.distance,
            FirebaseKeys.GOAL_HISTORY_TIME to dailyGoal.time,
            FirebaseKeys.GOAL_HISTORY_PATHS to dailyGoal.paths,
        )
    }

    /**
     * Helper function to initiate the chat preview of a given conversation
     * @param conversationId id of the new conversation
     * @param chatPreview object that contains the data of this preview
     * @return a future that indicate if the chat preview was correctly created
     */
    private fun initChatPreview(
        conversationId: String,
        chatPreview: ChatPreview,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val data = mapOf(
            FirebaseKeys.CHAT_TITLE to chatPreview.title,
            "${FirebaseKeys.CHAT_LAST_MESSAGE}/${chatPreview.lastMessage!!.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to
                chatPreview.lastMessage.senderId,
            "${FirebaseKeys.CHAT_LAST_MESSAGE}/${chatPreview.lastMessage.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to
                (chatPreview.lastMessage.content as MessageContent.Text?)?.text,
        ).filter { it.value != null }

        chatPreview(conversationId).updateChildren(data)
            .addOnSuccessListener {
                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
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
        val future = CompletableFuture<Unit>()

        val data = HashMap<String, Any>()
        for (member in membersList) {
            data[member] = true
        }

        chatMembers(conversationId).updateChildren(data)
            .addOnSuccessListener {
                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
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
        val future = CompletableFuture<Unit>()

        // the timestamp of the message is used as a key
        val data = mapOf(
            FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT to (firstMessage.content as MessageContent.Text?)?.text,
            FirebaseKeys.CHAT_MESSAGE_SENDER to firstMessage.senderId,
        ).filter { it.value != null }
        message(conversationId, firstMessage.timestamp).updateChildren(data)
            .addOnSuccessListener {
                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
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
        val future = CompletableFuture<Unit>()
        for (memberId in membersList) {
            val data = mapOf(conversationId to true)
            userProfile(memberId).child(FirebaseKeys.USER_CHATS).updateChildren(data)
                .addOnSuccessListener { future.complete(Unit) }
                .addOnFailureListener { future.completeExceptionally(it) }
        }

        return future
    }

    /**
     * Helper function to add a run message to a conversation
     * @param conversationId id of the conversation
     * @message that we want to add
     * @return a future to indicate if the message was correctly added
     */
    private fun addChatRunMessage(
        conversationId: String,
        message: Message,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val run = (message.content as MessageContent.RunPath).run
        val data = mapOf(
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN}/${run.getStartTime()}" to run,
        )
        chatMessages(conversationId).updateChildren(data)
            .addOnSuccessListener {
                // update the chat preview
                val lastMessage = mapOf(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN}/${run.getStartTime()}" to run,
                )
                // delete the previous last message and update it
                chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE).removeValue()
                    .addOnSuccessListener {
                        chatPreview(conversationId).updateChildren(lastMessage)
                            .addOnSuccessListener {
                                future.complete(Unit)
                            }.addOnFailureListener { future.completeExceptionally(it) }
                    }.addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    /**
     * Helper function to add a picture message to a conversation
     * @param conversationId id of the conversation
     * @message that we want to add
     * @return a future to indicate if the message was correctly added
     */
    private fun addChatPictureMessage(
        conversationId: String,
        message: Message,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val imageEncoded: String =
            Utils.encodePhotoToString((message.content as MessageContent.Picture).image)
        val data = mapOf(
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE}" to imageEncoded,
        )
        chatMessages(conversationId).updateChildren(data)
            .addOnSuccessListener { // update the chat preview
                val lastMessage = mapOf(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE}" to imageEncoded,
                )
                // delete the previous last message and update it
                chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE).removeValue()
                    .addOnSuccessListener {
                        chatPreview(conversationId).updateChildren(lastMessage)
                            .addOnSuccessListener {
                                future.complete(Unit)
                            }.addOnFailureListener { future.completeExceptionally(it) }
                    }.addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    /**
     * Helper function to add a text message to a conversation
     * @param conversationId id of the conversation
     * @message that we want to add
     * @return a future to indicate if the message was correctly added
     */
    private fun addChatTextMessage(
        conversationId: String,
        message: Message,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val data = listOf<Pair<String, Any?>>(
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to (message.content as MessageContent.Text).text,
        ).associate { entry -> entry }
        chatMessages(conversationId).updateChildren(data)
            .addOnSuccessListener { // update the chat preview
                val lastMessage = mapOf(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to message.content.text,
                )
                // delete the previous last message and update it
                chatPreview(conversationId).child(FirebaseKeys.CHAT_LAST_MESSAGE).removeValue()
                    .addOnSuccessListener {
                        chatPreview(conversationId).updateChildren(lastMessage)
                            .addOnSuccessListener {
                                future.complete(Unit)
                            }.addOnFailureListener { future.completeExceptionally(it) }
                    }.addOnFailureListener { future.completeExceptionally(it) }
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    private fun ilog(text: String) {
        Log.i("Firebase Database", text)
    }
}
