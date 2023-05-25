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
}
