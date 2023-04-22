package com.epfl.drawyourpath.community

import com.epfl.drawyourpath.path.Run
import java.time.LocalDateTime
import kotlin.collections.HashMap

data class TournamentPost(
    val user: String, // TODO change to a real user
    val run: Run,
    private var votes: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    private var userVotes: HashMap<String, Int> = HashMap(),
) : java.io.Serializable {

    /**
     * get the total number of votes
     *
     * @return the total number of votes
     */
    fun getVotes(): Int {
        return votes
    }

    /**
     * upvote the post if the user has not already upvoted otherwise remove the vote
     *
     * @param userId the userId
     * @return a boolean indicating if the upvote was done
     */
    fun upvote(userId: String): Boolean {
        val oldVote = userVotes.getOrDefault(userId, 0)
        if (oldVote == 1) {
            removeVote(userId)
            return false
        }
        votes += 1 - oldVote
        userVotes[userId] = 1
        return true
    }

    /**
     * downvote the post if the user has not already downvoted otherwise remove the vote
     *
     * @param userId the userId
     * @return a boolean indicating if the downvote was done
     */
    fun downvote(userId: String): Boolean {
        val oldVote = userVotes.getOrDefault(userId, 0)
        if (oldVote == -1) {
            removeVote(userId)
            return false
        }
        votes -= 1 + oldVote
        userVotes[userId] = -1
        return true
    }

    /**
     * remove the vote on the post if the user has already voted
     *
     * @param userId the userId
     */
    private fun removeVote(userId: String) {
        val oldVote = userVotes.getOrDefault(userId, 0)
        if (oldVote != 0) {
            votes -= oldVote
            userVotes.remove(userId)
        }
    }
}
