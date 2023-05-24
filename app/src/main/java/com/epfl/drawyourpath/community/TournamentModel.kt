package com.epfl.drawyourpath.community

import androidx.lifecycle.*
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

    // TODO delete this ----------------------------------
    private val _tournamentIds = mutableListOf("id1", "id2", "id3", "id4", "id5")
    private val tournamentIds: MutableLiveData<List<String>> = MutableLiveData(_tournamentIds)

    private fun getAllTournamentId(): LiveData<List<String>> {
        return tournamentIds
    }
    // TODO until here ------------------------------------

    private val allTournaments: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        addSource(getAllTournamentId()) { tournamentIds ->
            value = tournamentIds.mapNotNull { getTournament(it).value }
        }
    }

    val yourTournament: LiveData<List<Tournament>> = allTournaments.map { tournaments ->
        tournaments.filter { userTournaments.contains(it.id) && it.startDate <= LocalDateTime.now() }
    }

    val startingSoonTournament: LiveData<List<Tournament>> = allTournaments.map { tournaments ->
        tournaments.filter { it.startDate > LocalDateTime.now() }
    }

    val discoverTournament: LiveData<List<Tournament>> = allTournaments.map { tournaments ->
        tournaments.filter { !userTournaments.contains(it.id) && it.startDate <= LocalDateTime.now() }
    }

    private val postOf: MutableLiveData<String?> = MutableLiveData(null)

    val posts: LiveData<List<TournamentPost>> = postOf.switchMap { id ->
        id?.let { getTournamentPost(it) } ?: MediatorLiveData<List<TournamentPost>>().apply {
            addSource(allTournaments) { tournament ->
                value = tournament.mapNotNull { getTournamentPost(it.id).value }.flatten()
            }
        }
    }

    fun addTournament(tournament: Tournament){
        all_tournaments.add(tournament)
        _tournamentIds.add(tournament.id)
        tournamentIds.postValue(_tournamentIds)
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
        postOf.postValue(tournamentId)

    }

    // TODO replace by real tournaments
    // everything from here are samples

    private val userTournaments = mutableListOf("id1")

    private val all_tournaments = mutableListOf(sampleWeekly()).also { it.addAll(sampleYourTournaments()) }.also { it.addAll(sampleDiscoveryTournaments()) }
    private fun getTournament(id: String): LiveData<Tournament> {
        return MutableLiveData(all_tournaments.find { it.id == id }!!)
    }

    private fun getTournamentPost(id: String): LiveData<List<TournamentPost>> {
        return MutableLiveData(all_tournaments.find { it.id == id }!!.posts)
    }

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
            LocalDateTime.now().minusDays(3L),
            LocalDateTime.now().plusDays(10L),
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
                LocalDateTime.now().minusDays(3L),
                LocalDateTime.now().plusDays(4L),
                listOf(),
                posts1,
            ),
            Tournament(
                "id3",
                "time square",
                "draw a square",
                "creator3",
                LocalDateTime.now().minusDays(3L),
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
