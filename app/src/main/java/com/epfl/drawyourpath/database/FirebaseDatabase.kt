package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Base64
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
class FirebaseDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference

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
                    // If there is a past username.
                    if (exc2 == null) {
                        // And remove the old one.
                        nameMapping(pastUsername).removeValue { _, _ ->
                            future.complete(Unit)
                        }
                    } else {
                        future.complete(Unit)
                    }
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
        if (goals.distance != null && goals.distance <= 0.0) {
            return Utils.failedFuture(Exception("Distance must be greater than 0."))
        }

        if (goals.paths != null && goals.paths <= 0) {
            return Utils.failedFuture(Exception("Path must be greater than 0."))
        }

        if (goals.activityTime != null && goals.activityTime <= 0) {
            return Utils.failedFuture(Exception("Activity time must be greater than 0."))
        }

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

        Log.i(
            FirebaseDatabase::class.java.name,
            "Adding user $targetFriend as friend for $userId.",
        )

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
                val participantsIds = transformSnapshotKeysToStringList(data)
                // prepare changes in database
                // 1. remove the tournament from the list of tournaments of all participants
                val changes: MutableMap<String, Any?> = participantsIds.associate {
                    "${FirebaseKeys.USERS_ROOT}/$it/${FirebaseKeys.USER_TOURNAMENTS}/$tournamentId" to null
                }.toMutableMap()
                // 2. remove the tournament from the tournaments file
                changes.put("${FirebaseKeys.TOURNAMENTS_ROOT}/$tournamentId", null)
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
            val future = CompletableFuture<String>()
            future.completeExceptionally(Exception("Error in the generation of the conversation id !"))
            return future
        } else {
            val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
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
                    lastMessage = getMessageFromData(
                        data.child(FirebaseKeys.CHAT_LAST_MESSAGE).children.toMutableList().get(0),
                    ),
                )
                future.complete(preview)
            }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun setChatTitle(conversationId: String, newTitle: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val data = listOf<Pair<String, Any?>>(
            FirebaseKeys.CHAT_TITLE to newTitle,
        ).associate { entry -> entry }
        chatPreview(conversationId).updateChildren(data)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun getChatMemberList(conversationId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        chatMembers(conversationId).get().addOnSuccessListener { data ->
            val members = ArrayList<String>()
            for (elem in (data.value as Map<*, *>)) {
                members.add(elem.key as String)
            }
            future.complete(members)
        }.addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    override fun addChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        // add the new member to the members list
        val newMember = HashMap<String, Any>()
        newMember.put(userId, true)
        chatMembers(conversationId).updateChildren(newMember).addOnSuccessListener {
            // add the chat to the chat list of the user with userId
            val newChat = HashMap<String, Any>()
            newChat.put(conversationId, true)
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
            val listMessage = ArrayList<Message>()
            // Log.println(Log.INFO,"", data.children.toMutableList().get(0).toString())
            for (elem in data.children) {
                listMessage.add(getMessageFromData(elem))
            }
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
        val future = CompletableFuture<Unit>()
        future.completeExceptionally(Error("No type found for the content of the message !"))
        return future
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
                            val lastMessage = listOf<Pair<String, Any?>>(
                                "${FirebaseKeys.CHAT_MESSAGE_SENDER}" to data.child(FirebaseKeys.CHAT_MESSAGE_SENDER).value as String,
                                "${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to "This message was deleted",
                            ).associate { entry -> entry }
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
        val newMessage = listOf<Pair<String, Any?>>(
            "${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to message,
        ).associate { entry -> entry }
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
            runs = transformRunsHistory(profile.child(FirebaseKeys.RUN_HISTORY)),
            dailyGoals = transformDataToDailyGoalList(profile.child(FirebaseKeys.DAILY_GOALS)),
            friendList = transformSnapshotKeysToStringList(profile.child(FirebaseKeys.FRIENDS)),
            chatList = transformChatList(data.child(FirebaseKeys.USER_CHATS)),
        )
    }

    /**
     * Helper function to decode the photo from string to bitmap format and return null if the dataSnapShot is null
     * @param photoStr photo encoded
     * @return the photo in bitmap format, and null if no photo is stored on the database
     */
    private fun decodePhoto(photoStr: Any?): Bitmap? {
        return if (photoStr == null) {
            null
        } else {
            val tabByte = Base64.getDecoder().decode(photoStr as String)
            BitmapFactory.decodeByteArray(tabByte, 0, tabByte.size)
        }
    }

    /**
     * Helper function to obtain a list from the keys of a database snapshot.
     * @param data the data snapshot to be converted to a list
     * @return a list containing the keys of the snapshot
     */
    private fun transformSnapshotKeysToStringList(data: DataSnapshot?): List<String> {
        if (data == null) {
            return emptyList()
        }
        val stringList = ArrayList<String>()

        for (keyValue in data.children) {
            stringList.add(keyValue.key as String)
        }

        Log.i(
            FirebaseDatabase::class.java.name,
            String.format("List has %d elements.", stringList.size),
        )

        return stringList
    }

    /**
     * Helper function to obtain the runs history from the database of the user
     * @param data the data snapshot containing the history
     * @return a list containing the history of the runs of the user
     */
    private fun transformRunsHistory(data: DataSnapshot?): List<Run> {
        if (data == null) {
            return emptyList()
        }
        val runsHistory = ArrayList<Run>()

        for (run in data.children) {
            val transformRun = transformRun(run)
            if (transformRun != null) {
                runsHistory.add(transformRun)
            } else {
                Log.w(
                    FirebaseDatabase::class.java.name,
                    "A point of a run has invalid coordinates => ignoring the point",
                )
            }
        }

        return runsHistory
    }

    /**
     * Helper function to retrieve a run object from the database
     * @param data the datasnapshot containing the run
     * @return the run corresponding to the data
     */
    private fun transformRun(data: DataSnapshot): Run? {
        val points = ArrayList<LatLng>()
        for (point in data.child("path").child("points").children) {
            val lat = point.child("latitude").getValue(Double::class.java)
            val lon = point.child("longitude").getValue(Double::class.java)
            if (lat != null && lon != null) {
                points.add(LatLng(lat, lon))
            }
        }
        val startTime = data.child("startTime").value as? Long
        val endTime = data.child("endTime").value as? Long
        if (startTime != null && endTime != null) {
            return Run(Path(points), startTime, endTime)
        }
        return null
    }

    /**
     * Helper function to obtain the chats list from the database of the user
     * @param data the data snapshot containing the chats List
     * @return a list containing the conversationId of all the chats where the user is present
     */
    private fun transformChatList(data: DataSnapshot?): List<String> {
        if (data == null) {
            return emptyList()
        }
        val chatListConversationId = ArrayList<String>()

        for (chat in data.children) {
            chatListConversationId.add(chat.key as String)
        }
        return chatListConversationId
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
        userRoot(currentUserId).child(FirebaseKeys.FRIENDS)
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
        userRoot(currentUserId).child(FirebaseKeys.FRIENDS).child(removeUserId).removeValue()
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
     * Helper function to obtain the daily goal list from the database of the user
     * @param data the data snapshot containing the daily goal list
     * @return a list containing the daily goal realized by the user
     */
    private fun transformDataToDailyGoalList(data: DataSnapshot?): List<DailyGoal> {
        if (data == null) {
            return emptyList()
        }

        val dailyGoalList = ArrayList<DailyGoal>()

        for (dailyGoal in data.children) {
            val date: LocalDate =
                (if (dailyGoal.key != null) LocalDate.ofEpochDay(dailyGoal.key!!.toLong()) else null)
                    ?: continue

            val expectedDistance: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE)
                    .getValue(Double::class.java)
            val expectedTime: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME)
                    .getValue(Double::class.java)
            val expectedPaths: Int? =
                (dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS).value as Long?)?.toInt()
            val distance: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_DISTANCE).getValue(Double::class.java)
            val time: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_TIME).getValue(Double::class.java)
            val paths: Int? =
                (dailyGoal.child(FirebaseKeys.GOAL_HISTORY_PATHS).value as Long?)?.toInt()

            dailyGoalList.add(
                DailyGoal(
                    expectedDistance = expectedDistance ?: 0.0,
                    expectedPaths = expectedPaths ?: 0,
                    expectedTime = expectedTime ?: 0.0,
                    distance = distance ?: 0.0,
                    time = time ?: 0.0,
                    paths = paths ?: 0,
                    date = date,
                ),
            )
        }
        return dailyGoalList
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

        val data = listOf<Pair<String, Any?>>(
            "${FirebaseKeys.CHAT_TITLE}" to chatPreview.title,
            "${FirebaseKeys.CHAT_LAST_MESSAGE}/${chatPreview.lastMessage!!.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to chatPreview.lastMessage.senderId,
            "${FirebaseKeys.CHAT_LAST_MESSAGE}/${chatPreview.lastMessage.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to (chatPreview.lastMessage.content as MessageContent.Text).text,
        ).filter { it.second != null }.associate { entry -> entry }

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
            data.put(member, true)
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
        val data = listOf<Pair<String, Any?>>(
            "${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to (firstMessage.content as MessageContent.Text).text,
            "${FirebaseKeys.CHAT_MESSAGE_SENDER}" to firstMessage.senderId,
        ).filter { it.second != null }.associate { entry -> entry }
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
            val data = HashMap<String, Any>()
            data.put(conversationId, true)
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
        val data = listOf<Pair<String, Any?>>(
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN}/${run.getStartTime()}" to run,
        ).associate { entry -> entry }
        chatMessages(conversationId).updateChildren(data)
            .addOnSuccessListener {
                // update the chat preview
                val lastMessage = listOf<Pair<String, Any?>>(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN}/${run.getStartTime()}" to run,
                ).associate { entry -> entry }
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
        val byteArray = ByteArrayOutputStream()
        (message.content as MessageContent.Picture).image.compress(
            Bitmap.CompressFormat.WEBP,
            70,
            byteArray,
        )
        val imageEncoded: String = Base64.getEncoder().encodeToString(byteArray.toByteArray())
        val data = listOf<Pair<String, Any?>>(
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
            "${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE}" to imageEncoded,
        ).associate { entry -> entry }
        chatMessages(conversationId).updateChildren(data)
            .addOnSuccessListener { // update the chat preview
                val lastMessage = listOf<Pair<String, Any?>>(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE}" to imageEncoded,
                ).associate { entry -> entry }
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
                val lastMessage = listOf<Pair<String, Any?>>(
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_SENDER}" to message.senderId,
                    "${FirebaseKeys.CHAT_LAST_MESSAGE}/${message.timestamp}/${FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT}" to message.content.text,
                ).associate { entry -> entry }
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
     * Helper function to transform some data into a message
     * @param data datasnpshot that contains the data of the message
     * @return a message correposnding to this data
     */
    private fun getMessageFromData(data: DataSnapshot): Message {
        val dateStr: String =
            data.key ?: throw Exception("There content of this data not correspond to a message")
        val date = dateStr.toLong()
        val sender = data.child(FirebaseKeys.CHAT_MESSAGE_SENDER).value as String
        val dataImage = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_IMAGE)
        val dataRun = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_RUN)
        val dataText = data.child(FirebaseKeys.CHAT_MESSAGE_CONTENT_TEXT)
        if (dataImage.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.Picture(decodePhoto(dataImage.value as String)!!),
                timestamp = date,
            )
        }
        if (dataRun.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.RunPath(
                    transformRun(
                        dataRun.children.toMutableList().get(0),
                    )!!,
                ),
                timestamp = date,
            )
        }
        if (dataText.value != null) {
            return Message(
                id = date,
                senderId = sender,
                content = MessageContent.Text(dataText.value as String),
                timestamp = date,
            )
        }
        throw Error("The content of the message correspond to any type !")
    }

    private fun ilog(text: String) {
        Log.i("Firebase Database", text)
    }
}
