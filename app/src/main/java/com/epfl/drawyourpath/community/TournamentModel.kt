package com.epfl.drawyourpath.community

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.path.Run
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * this class is used to link the database to the UI for the tournaments
 */
class TournamentModel : ViewModel() {
    // use mock by default
    private var database: Database = FirebaseDatabase()

    private var currentUserId: String = MockDatabase.mockUser.userId!!

    private val userTournaments: MutableList<String> = mutableListOf()

    private val allTournamentIds = database.getAllTournamentsId()

    private var allTournamentInfo: MutableMap<String, LiveData<Tournament>> = mutableMapOf()

    private val allTournaments: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        addSource(allTournamentIds) { tournamentIds ->
            tournamentIds.forEach { id ->
                allTournamentInfo.getOrPut(id) {
                    database.getTournamentInfo(id).also { livedata ->
                        addSource(livedata) { tournament ->
                            value = value?.filterNot { it.id == tournament.id }?.plusElement(tournament) ?: listOf(tournament)
                        }
                    }
                }
            }
            value = allTournamentInfo.values.mapNotNull { it.value }
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
        id?.let { database.getTournamentPosts(it) } ?: MutableLiveData(listOf())/*MediatorLiveData<List<TournamentPost>>().apply {
            addSource(allTournaments) { tournaments ->
                value = tournaments.mapNotNull { tournament ->
                    database.getTournamentPosts(tournament.id).also { addSource(it) { /* TODO */ } }.value
                }.flatten()
            }
        }*/
    }

    /**
     * create the new tournament to store in the database
     * show toast message to tell if it is a success or a failure
     * @param tournament the new tournament to add to the database
     * @param context to show the toast message
     */
    fun createTournament(tournament: Tournament.TournamentParameters, context: Context): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            val tid = database.getTournamentUniqueId() ?: throw Error("Can't get a tournament id!")
            Tournament(
                id = tid,
                name = tournament.name,
                description = tournament.description,
                creatorId = currentUserId,
                startDate = tournament.startDate,
                endDate = tournament.endDate,
                visibility = tournament.visibility,
            )
        }.thenComposeAsync {
            database.addTournament(it)
        }.thenApplyAsync {
            Toast.makeText(context, "Tournament created!", Toast.LENGTH_LONG).show()
        }.exceptionally {
            Toast.makeText(context, "Operation failed: ${it.message}", Toast.LENGTH_LONG,).show()
        }
    }

    fun addPost(tournamentId: String, run: Run) {

    }

    fun addVote(vote: Int, postId: String) {
        return
    }

    /**
     * change the posts variable to show only posts from a specific tournament or from all tournament
     * @param tournamentId the id of the tournament or null for all tournament
     */
    fun showPostOf(tournamentId: String?) {
        postOf.postValue(tournamentId)
    }

    /**
     * set the current user
     * @param userId the user id of the current user
     */
    fun setCurrentUser(userId: String) {
        currentUserId = userId
    }
}
