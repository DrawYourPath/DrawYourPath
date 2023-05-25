package com.epfl.drawyourpath.community

import android.content.Context
import android.util.Log
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

    private val _userTournaments: MutableList<String> = MockDatabase.mockUser.tournaments!!.toMutableList()

    private val userTournaments: MutableLiveData<List<String>> = MutableLiveData(_userTournaments)

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

    val yourTournament: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { ids.contains(it.id) && it.startDate <= LocalDateTime.now() }
        }
    }

    val startingSoonTournament: LiveData<List<Pair<Tournament, Boolean>>> = MediatorLiveData<List<Pair<Tournament, Boolean>>>().apply {
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter {  it.startDate > LocalDateTime.now() }.map { Pair(it, userTournaments.value!!.contains(it.id)) }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { it.startDate > LocalDateTime.now() }.map { Pair(it, ids.contains(it.id)) }
        }
    }

    val discoverTournament: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { !userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { !ids.contains(it.id) && it.startDate <= LocalDateTime.now() }
        }
    }

    private val postOf: MutableLiveData<String?> = MutableLiveData(null)

    private val allPosts: MutableMap<String, LiveData<List<TournamentPost>>> = mutableMapOf()

    val posts: LiveData<List<TournamentPost>> = postOf.switchMap { id ->
        id?.let { database.getTournamentPosts(it) } ?: MediatorLiveData<List<TournamentPost>>().apply {
            addSource(userTournaments) { ids ->
                value = allPosts.filterKeys { ids.contains(it) }.values.mapNotNull { it.value }.flatten()
            }
            addSource(allTournamentIds) { tournamentIds ->
                tournamentIds.forEach { id ->
                    allPosts.getOrPut(id) {
                        database.getTournamentPosts(id).also { livedata ->
                            addSource(livedata) { posts ->
                                if (userTournaments.value!!.contains(id)) {
                                    value = value?.filterNot { post -> posts.map { it.postId }.contains(post.postId) }?.plus(posts) ?: posts
                                }
                            }
                        }
                    }
                }
                value = allPosts.filterKeys { userTournaments.value!!.contains(it) }.values.mapNotNull { it.value }.flatten()
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
     * @param tournament the tournament where to post the tournament post
     * @param run the run to post
     * @param context to show the toast message
     */
    fun addPost(tournament: Tournament, run: Run, context: Context): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            val pid = database.getPostUniqueId() ?: throw Error("Can't get a tournament id!")
            TournamentPost(
                postId = pid,
                tournamentId = tournament.id,
                tournamentName = tournament.name,
                userId = currentUserId,
                run = run,
            )
        }.thenComposeAsync {
            database.addPostToTournament(tournament.id, it)
        }.thenApplyAsync {
            Toast.makeText(context, "Post created!", Toast.LENGTH_LONG).show()
        }.exceptionally {
            Toast.makeText(context, "Operation failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * register or unregister from a tournament
     * @param tournamentId the tournament to register/unregister to
     * @param register true to register or false to unregister
     */
    fun register(tournamentId: String, register: Boolean): CompletableFuture<Unit> {
        val future = CompletableFuture.supplyAsync {  }
        if (register) {
            future.thenComposeAsync {
                database.addUserToTournament(currentUserId, tournamentId)
            }
        } else {
            future.thenComposeAsync {
                database.removeUserFromTournament(currentUserId, tournamentId)
            }
        }
        return future.thenApplyAsync {
            // once stored in database wait to get updated value
            Thread.sleep(500)
            updateUserTournaments()
        }
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

    /**
     * set the current user
     * @param userId the user id of the current user
     */
    fun setCurrentUser(userId: String) {
        currentUserId = userId
        updateUserTournaments(true)
    }

    private fun updateUserTournaments(firstClear: Boolean = false): CompletableFuture<Unit> {
        if (firstClear) {
            _userTournaments.clear()
            userTournaments.postValue(_userTournaments)
        }
        return database.getUserData(currentUserId).thenApplyAsync {
            Log.d("test", it.tournaments.toString())
            it.tournaments?.let { it1 ->
                _userTournaments.clear()
                _userTournaments.addAll(it1)
                userTournaments.postValue(_userTournaments)
            }
        }
    }

}
