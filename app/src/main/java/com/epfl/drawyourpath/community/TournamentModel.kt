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

    private val userTournaments: MutableList<String> = mutableListOf("-NWH9CjMRBKNtn88SLHG")

    private val allTournamentIds = database.getAllTournamentsId()

    private val allTournamentInfo: MutableMap<String, LiveData<Tournament>> = mutableMapOf()

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

    private val allPosts: MutableMap<String, LiveData<List<TournamentPost>>> = mutableMapOf()

    val posts: LiveData<List<TournamentPost>> = postOf.switchMap { id ->
        id?.let { database.getTournamentPosts(it) } ?: MediatorLiveData<List<TournamentPost>>().apply {
            addSource(allTournamentIds) { tournamentIds ->
                tournamentIds.filter {
                    userTournaments.contains(it)
                }.forEach { id ->
                    allPosts.getOrPut(id) {
                        database.getTournamentPosts(id).also { livedata ->
                            addSource(livedata) { posts ->
                                value = value?.filterNot { post -> posts.map { it.postId }.contains(post.postId) }?.plus(posts) ?: posts
                            }
                        }
                    }
                }
                value = allPosts.values.mapNotNull { it.value }.flatten()
            }
        }
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
            Toast.makeText(context, "Operation failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * create the new post to store in the database
     * show toast message to tell if it is a success or a failure
     * @param tournamentId the tournament where to post the tournament post
     * @param run the run to post
     * @param context to show the toast message
     */
    fun addPost(tournamentId: String, run: Run, context: Context): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            val pid = database.getPostUniqueId() ?: throw Error("Can't get a tournament id!")
            TournamentPost(
                postId = pid,
                userId = currentUserId,
                run = run,
            )
        }.thenComposeAsync {
            database.addPostToTournament(tournamentId, it)
        }.thenApplyAsync {
            Toast.makeText(context, "Post created!", Toast.LENGTH_LONG).show()
        }.exceptionally {
            Toast.makeText(context, "Operation failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
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
