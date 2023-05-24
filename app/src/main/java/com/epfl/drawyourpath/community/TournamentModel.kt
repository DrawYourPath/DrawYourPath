package com.epfl.drawyourpath.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * this class is used to link the database to the UI for the tournaments
 */
class TournamentModel : ViewModel() {

    private var database: Database = FirebaseDatabase()

    private val _yourTournament: MutableLiveData<List<Tournament>> = MutableLiveData(listOf(sampleWeekly()))
    val yourTournament: LiveData<List<Tournament>> = _yourTournament

    private val _startingSoonTournament: MutableLiveData<List<Tournament>> = MutableLiveData(sampleYourTournaments())
    val startingSoonTournament: LiveData<List<Tournament>> = _startingSoonTournament

    private val _discoverTournament: MutableLiveData<List<Tournament>> = MutableLiveData(sampleDiscoveryTournaments())
    val discoverTournament: LiveData<List<Tournament>> = _discoverTournament

    private val _posts: MutableLiveData<List<TournamentPost>> = MutableLiveData(sampleWeekly().posts)
    val posts: LiveData<List<TournamentPost>> = _posts

    fun addTournament(tournament: Tournament): CompletableFuture<Unit> {
        return CompletableFuture()
    }

    fun addPost(tournamentId: String, post: TournamentPost): CompletableFuture<Unit> {
        return CompletableFuture()
    }

    fun addVote(vote: Int, postId: String): CompletableFuture<Unit> {
        return CompletableFuture()
    }

    /**
     * change the posts variable to show only posts from a specific tournament or from all tournament
     * @param tournamentId the id of the tournament or null for all tournament
     */
    fun showPostOf(tournamentId: String?) {
        return
    }

    // TODO replace by real tournaments
    // everything from here are samples

    /**
     * sample tournaments
     */
    private fun sampleWeekly(): Tournament {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0),
        )

        return Tournament(
            "id1",
            "Star Path",
            "draw a star path",
            "creator1",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            listOf(),
            posts,
        )
    }

    /**
     * sample tournaments
     */
    private fun sampleYourTournaments(): List<Tournament> {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0),
        )
        val posts1 = mutableListOf(
            TournamentPost("xD c moi", sampleRun(), 35),
            TournamentPost("Jaqueline", sampleRun(), 356),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("me", sampleRun(), -563),
            TournamentPost("IDK", sampleRun(), 0),
        )
        return mutableListOf(
            Tournament(
                "id2",
                "best tournament ever",
                "draw whatever you want",
                "creator2",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                listOf(),
                posts1,
            ),
            Tournament(
                "id3",
                "time square",
                "draw a square",
                "creator3",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                listOf(),
                posts,
            ),
        )
    }

    /**
     * sample tournaments
     */
    private fun sampleDiscoveryTournaments(): List<Tournament> {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0),
        )
        val posts1 = mutableListOf(
            TournamentPost("SpaceMan", sampleRun(), 35),
            TournamentPost("NASA", sampleRun(), 124),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("Alien", sampleRun(), -3),
            TournamentPost("IDK", sampleRun(), 0),
        )
        return mutableListOf(
            Tournament(
                "id4",
                "Discover the earth",
                "draw the earth",
                "creator4",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                listOf(),
                posts1,
            ),
            Tournament(
                "id5",
                "to the moon",
                "draw the moon",
                "creator5",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                listOf(),
                posts,
            ),
        )
    }

    /**
     * sample run
     */
    private fun sampleRun(): Run {
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(point1, point2)
        val path = Path(listOf(points))
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10
        return Run(path = path, startTime = startTime, endTime = endTime, duration = 10)
    }
}
