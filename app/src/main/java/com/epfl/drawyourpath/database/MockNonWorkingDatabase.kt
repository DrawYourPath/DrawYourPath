package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.milestone.MilestoneEnum
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Run
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * this class is used for testing and will always return failed futures
 */
class MockNonWorkingDatabase : Database() {
    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun isTournamentInDatabase(tournamentId: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun isPostInDatabase(
        tournamentId: String,
        postId: String,
    ): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun getUsername(userId: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setUsername(userId: String, username: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getUserData(userId: String): CompletableFuture<UserData> {
        return failedFuture()
    }

    override fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addTrophy(userId: String, trophy: Trophy): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addMilestone(
        userId: String,
        milestone: MilestoneEnum,
        date: LocalDate,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getAllTournamentsId(): LiveData<List<String>> {
        throw Exception("")
    }

    override fun getTournamentUniqueId(): String? {
        return null
    }

    override fun addTournament(tournament: Tournament): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeTournament(tournamentId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addUserToTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeUserFromTournament(
        userId: String,
        tournamentId: String,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getTournament(tournamentId: String): LiveData<Tournament> {
        throw Exception("")
    }

    override fun getTournamentPosts(tournamentId: String): LiveData<List<TournamentPost>> {
        throw Exception("")
    }

    override fun getTournamentParticipantsId(tournamentId: String): CompletableFuture<List<String>> {
        return failedFuture()
    }

    override fun getTournamentInfo(tournamentId: String): LiveData<Tournament> {
        throw Exception("")
    }

    override fun getPostUniqueId(): String? {
        return null
    }

    override fun addPostToTournament(
        tournamentId: String,
        post: TournamentPost,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun voteOnPost(
        userId: String,
        tournamentId: String,
        postId: String,
        vote: Int,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun createChatConversation(
        name: String,
        membersList: List<String>,
        creatorId: String,
        welcomeMessage: String,
    ): CompletableFuture<String> {
        return failedFuture()
    }

    override fun getChatPreview(conversationId: String): LiveData<ChatPreview> {
        throw Exception("")
    }

    override fun getChatList(userId: String): LiveData<List<String>> {
        throw Exception("")
    }

    override fun getFriendsList(userId: String): LiveData<List<String>> {
        throw Exception("")
    }

    override fun setChatTitle(conversationId: String, newTitle: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getChatMemberList(conversationId: String): CompletableFuture<List<String>> {
        return failedFuture()
    }

    override fun addChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeChatMember(userId: String, conversationId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getChatMessages(conversationId: String): LiveData<List<Message>> {
        throw Exception("")
    }

    override fun addChatMessage(conversationId: String, message: Message): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeChatMessage(
        conversationId: String,
        messageId: Long,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun modifyChatTextMessage(
        conversationId: String,
        messageId: Long,
        message: String,
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    private fun <T : Any> failedFuture(): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { throw Error(ERROR_NAME) }
    }

    companion object {
        const val ERROR_NAME = "MockError: No Internet Connection"
    }
}
