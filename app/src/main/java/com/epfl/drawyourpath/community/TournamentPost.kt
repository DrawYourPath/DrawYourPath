package com.epfl.drawyourpath.community

import com.epfl.drawyourpath.path.Run
import com.google.firebase.database.Exclude
import java.time.LocalDateTime
import kotlin.collections.HashMap

data class TournamentPost(
    val postId: String,
    val tournamentId: String,
    val tournamentName: String,
    val userId: String,
    val run: Run,
    val date: LocalDateTime = LocalDateTime.now(),
    private var usersVotes: MutableMap<String, Int> = HashMap(),
) {

    private var votes = usersVotes.values.sum()

    /**
     * get the total number of votes
     *
     * @return the total number of votes
     */
    @Exclude
    fun getVotes(): Int {
        return votes
    }

    /**
     * get the different users' votes (do not remove: used by FireBase)
     *
     * @return the users' votes
     */
    fun getUsersVotes(): Map<String, Int> {
        return usersVotes.toMap()
    }

    /**
     * upvote the post if the user has not already upvoted otherwise remove the vote
     *
     * @param userId the userId
     * @return a boolean indicating if the upvote was done
     */
    fun upvote(userId: String): Boolean {
        val oldVote = usersVotes.getOrDefault(userId, 0)
        if (oldVote == 1) {
            removeVote(userId)
            return false
        }
        votes += 1 - oldVote
        usersVotes[userId] = 1
        return true
    }

    /**
     * downvote the post if the user has not already downvoted otherwise remove the vote
     *
     * @param userId the userId
     * @return a boolean indicating if the downvote was done
     */
    fun downvote(userId: String): Boolean {
        val oldVote = usersVotes.getOrDefault(userId, 0)
        if (oldVote == -1) {
            removeVote(userId)
            return false
        }
        votes -= 1 + oldVote
        usersVotes[userId] = -1
        return true
    }

    /**
     * remove the vote on the post if the user has already voted
     *
     * @param userId the userId
     */
    private fun removeVote(userId: String) {
        val oldVote = usersVotes.getOrDefault(userId, 0)
        if (oldVote != 0) {
            votes -= oldVote
            usersVotes.remove(userId)
        }
    }
}
