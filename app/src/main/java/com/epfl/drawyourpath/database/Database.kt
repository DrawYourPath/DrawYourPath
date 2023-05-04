package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.concurrent.CompletableFuture

data class UserGoals(
    val paths: Long? = null,
    val distance: Double? = null,
    val activityTime: Double? = null,
)

data class UserData(
    val username: String? = null,
    val userId: String? = null,
    val firstname: String? = null,
    val surname: String? = null,
    val birthDate: Long? = null,
    val email: String? = null,
    val goals: UserGoals? = null,
    val picture: String? = null,
    val friendList: List<String>? = null,
    val runs: List<Run>? = null,
    val dailyGoals: List<DailyGoal>? = null,
    val chatList: List<String>? = null,
)

data class ChatPreview(
    val conversationId: String? = null,
    val title: String? = null,
    val lastMessage: Message? = null,
)

data class ChatMembers(
    val conversationId: String? = null,
    val membersList: List<String>? = null,
)

data class ChatMessages(
    val conversationId: String? = null,
    val chat: List<Message>? = null,
)

abstract class Database {
    /**
     * This function is used to know if a certain user is already store in the database
     * @param userId that correspond to the user
     * @return a future that indicate if the user is store on the database
     */
    abstract fun isUserInDatabase(userId: String): CompletableFuture<Boolean>

    /**
     * This function will return a future that give the username in function of the userId
     * @param userId of the user
     * @return a future that give the username of the user
     */
    abstract fun getUsername(userId: String): CompletableFuture<String>

    /**
     * This function will return a future that give the userId in function of the username
     * @param username of the user
     * @return a future that give the userId of the user
     */
    abstract fun getUserIdFromUsername(username: String): CompletableFuture<String>

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    abstract fun isUsernameAvailable(userName: String): CompletableFuture<Boolean>

    /**
     * Sets the username of the specified user. Fails if the username is already taken.
     * @param userId The userId of the target user.
     * @param username username that the user want to set in the database
     * @return a future that indicate if the user account has been successfully created
     */
    abstract fun setUsername(userId: String, username: String): CompletableFuture<Unit>

    /**
     * Creates a new user in the database.
     */
    abstract fun createUser(userId: String, userData: UserData): CompletableFuture<Unit>

    /**
     * This function is used to initialize the user profile information with UserModel give in parameter
     * @param userData used for the initialization of the user profile
     * @return a future that indicate if the user profile has been correctly initiate
     */
    abstract fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit>

    /**
     * This function is used to get the user account of the user with userId
     * @param userId of the user that we want to retrieve is account
     * @return a future that return the UserModel corresponding to this user account
     */
    abstract fun getUserData(userId: String): CompletableFuture<UserData>

    /**
     * Sets the goals for this user.
     * @param goals the goals we want to update. Passing null for a goal won't update it.
     * @return a future that indicate if the goal has been correctly set to the database
     */
    abstract fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit>

    /**
     * This function will set the profilePhoto to the database (Bitmap) of the user logged
     * @param userId The userId of the user we want to set the photo.
     * @param photo that will be set
     * @return a future that indicate if the photo has been correctly set to the database
     */
    abstract fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit>

    /**
     * This function will add a user to the the friends list of the current user with his userId if this user is present on the database
     * @param userId Id of the current user.
     * @param targetFriend of the user that we want to add to the friendsList of the current user
     * @throws Exception an Error if the user that we want to added to the friends list is not present on the database.
     * @return a future that indicate if the user has been correctly added to the current user friends list
     */
    abstract fun addFriend(userId: String, targetFriend: String): CompletableFuture<Unit>

    /**
     * This function will remove a user to the the friends list of the current user with his userId
     * @param targetFriend of the user that we want to remove to the friendsList of the current user
     * @throws an Error if the user that we want to removed is not present on the database.
     * @return a future that indicate if the user has been correctly removed to the current user friends list
     */
    abstract fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit>

    /**
     * This function will add a run to the history of runs, using its starting time as a key
     * @param userId The target user.
     * @param run to be stored
     * @return a future that indicate if the run has been correctly added to the history in the database
     */
    abstract fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit>

    /**
     * This function will remove a run from the history of runs, using its starting time as a key
     * @param userId The target user.
     * @param run to be removed
     * @return a future that indicate if the run has been correctly removed from the history in the database
     */
    abstract fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit>

    /**
     * This function is used to add a dailyGoal in the database to the list of dailyGoals realized by the user logged
     * in the dailyGoals section(the dailyGoal will be update if dailyGoal at this date already exist in the database).
     * @param userId The target user.
     * @param dailyGaol that we want to add in the database
     * @return a future that indicate if the daily Goal have been correctly added to the database
     */
    abstract fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit>

    /**
     * Function used to update on the database the user achievements(total distance, total activity time and total nb of paths draw by the user)
     * with the result at the end of a drawing activity(remark: the total number of path will be incremented by one, since only one draw
     * can be achieved each drawing activity).
     * @param distanceDrawing distance run by user to achieve the drawing
     * @param activityTimeDrawing time take by the user to realized the drawing
     * @return a future that indicate if the achievements of the user have been correctly updated.
     */
    abstract fun updateUserAchievements(userId: String, distanceDrawing: Double, activityTimeDrawing: Double): CompletableFuture<Unit>

    /**
     * Function used to create a chat conversation with other users of the DrawYourPath community.
     * @param name name of the chat conversation
     * @param membersList list of the members userId of the chat conversation(the creator must be included in this list)
     * @param creatorId userId of the conversation creator
     * @param welcomeMessage a welcome message in a string format
     * @return a future that gives the conversationId of the new conversation created
     */
    abstract fun createChatConversation(
        name: String,
        membersList: List<String>,
        creatorId: String,
        welcomeMessage: String,
    ): CompletableFuture<String>

    /**
     * Function used to obtain the chat preview of a given conversation with his conversationId
     * @param conversationId that we want to obtain the chat preview
     * @return a future containing a chat preview object that contains the information of the chat preview
     */
    abstract fun getChatPreview(conversationId: String): CompletableFuture<ChatPreview>

    /**
     * Function used to modify the title of a given conversation with his conversationId
     * @param conversationId that we want to modify the name
     * @param newTitle new title of the conversation
     * @return a future that indicate if the name of the conversation was correctly modify.
     */
    abstract fun setChatTitle(conversationId: String, newTitle: String): CompletableFuture<Unit>

    /**
     * Function used to get the members list of a given conversation with his conversationId
     * @param conversationId that we want to retrieve the members list
     * @return a future that contains the members list (a list of their userId) of the conversation
     */
    abstract fun getChatMemberList(conversationId: String): CompletableFuture<List<String>>

    /**
     * Function used to add a member in a given conversation with his conversation Id
     * @param userId userId of the member that we want to add to the conversation
     * @param conversationId of the conversation
     * @return a future that indicate if the new member was correctly added
     */
    abstract fun addChatMember(userId: String, conversationId: String): CompletableFuture<Unit>

    /**
     * Function used to remove a member in a given conversation with his conversation Id
     * @param userId userId of the member that we want to remove to the conversation
     * @param conversationId of the conversation
     * @return a future that indicate if the member was correctly removed
     */
    abstract fun removeChatMember(userId: String, conversationId: String): CompletableFuture<Unit>

    /**
     * Function to get the chat messages of a given conversation with his conversation Id
     * @param conversationId of the conversation
     * @return a future that contains the messages of the given conversation
     */
    abstract fun getChatMessages(conversationId: String): CompletableFuture<List<Message>>

    /**
     * Function used to add a message to the messages list of a given conversation with his conversationId
     * @param conversationId of the conversation
     * @param message that we want to add to the conversation
     * @return a future that indicated if the message was correctly added to the database
     */
    abstract fun addChatMessage(conversationId: String, message: Message): CompletableFuture<Unit>

    /**
     * Function used to remove a message(with a given timestamp) from a conversation with his id
     * @param conversationId of the conversation
     * @param messageId of the message that we want to removed thant also correspond to the timestamp of the message(in epoch seconds)
     * @return a future that indicated if the message was correctly deleted
     */
    abstract fun removeChatMessage(conversationId: String, messageId: Long): CompletableFuture<Unit>

    /**
     * Function used to modify a text message(with a given timestamp) from a conversation with his id
     * @param conversationId of the conversation
     * @param messageId of the text message that we want to modify that also correspond to the timestamp of the message(in epoch seconds)
     * @param message the content text of the new message
     * @return a future that indicated if the message was correctly modify
     */
    abstract fun modifyChatTextMessage(conversationId: String, messageId: Long, message: String): CompletableFuture<Unit>
}
