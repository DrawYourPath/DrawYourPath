package com.epfl.drawyourpath.community

import com.github.drawyourpath.bootcamp.path.Run
import java.time.LocalDateTime

data class TournamentPost(
    val user: String, //TODO change to a real user
    val run: Run,
    private var votes: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    private var userVotes: HashMap<String, Int> = HashMap(),
): java.io.Serializable {

    fun getVotes(): Int {
        return votes
    }

    fun upvote(userId: String) {
        val oldVote = userVotes.getOrDefault(userId, 0)
        if (oldVote != 1) {
            votes += 1 - oldVote
            userVotes[userId] = 1
        }
    }

    fun downvote(userId: String) {
        val oldVote = userVotes.getOrDefault(userId, 0)
        if (oldVote != -1) {
            votes -= 1 + oldVote
            userVotes[userId] = -1
        }
    }


}
