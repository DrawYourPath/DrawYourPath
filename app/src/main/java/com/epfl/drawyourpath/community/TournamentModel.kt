package com.epfl.drawyourpath.community

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.path.Run
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * this class is used to link the database to the UI for the tournaments
 */
class TournamentModel(
    val database: Database,
    private var currentUserId: String,
) : ViewModel() {
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
            value = tournaments.filter { userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { ids.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
    }

    val startingSoonTournament: LiveData<List<Pair<Tournament, Boolean>>> = MediatorLiveData<List<Pair<Tournament, Boolean>>>().apply {
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { it.startDate > LocalDateTime.now() && it.endDate >= LocalDateTime.now() }.map { Pair(it, userTournaments.value!!.contains(it.id)) }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { it.startDate > LocalDateTime.now() && it.endDate >= LocalDateTime.now() }.map { Pair(it, ids.contains(it.id)) }
        }
    }

    val discoverTournament: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { !userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { !ids.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
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
        val future = CompletableFuture.supplyAsync {
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
        }
        future.whenComplete { _, error ->
            if (error == null) {
                Toast.makeText(context, "Tournament created!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Operation failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
        return future
    }

    /**
     * create the new post to store in the database
     * show toast message to tell if it is a success or a failure
     * @param tournament the tournament where to post the tournament post
     * @param run the run to post
     * @param context to show the toast message
     */
    fun createPost(tournament: Tournament?, run: Run?, context: Context): CompletableFuture<Unit> {
        val future = CompletableFuture.supplyAsync {
            val pid = database.getPostUniqueId() ?: throw Error("Can't get a tournament id!")
            val tournamentNotNull = tournament ?: throw Error("Can't get a tournament!")
            val runNotNull = run ?: throw Error("Can't get a run!")
            TournamentPost(
                postId = pid,
                tournamentId = tournamentNotNull.id,
                tournamentName = tournamentNotNull.name,
                userId = currentUserId,
                run = runNotNull,
            )
        }.thenComposeAsync {
            database.addPostToTournament(tournament!!.id, it)
        }
        future.whenComplete { _, error ->
            if (error == null) {
                Toast.makeText(context, "Post created!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Operation failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
        return future
    }

    /**
     * register or unregister from a tournament
     * @param tournamentId the tournament to register/unregister to
     * @param register true to register or false to unregister
     */
    fun register(tournamentId: String, register: Boolean): CompletableFuture<Unit> {
        val future = CompletableFuture.supplyAsync { }
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

    /**
     * add a vote to a post
     * @param vote the vote (-1, 0, 1)
     * @param postId the post
     * @param tournamentId the tournament
     */
    fun addVote(vote: Int, postId: String, tournamentId: String): CompletableFuture<Unit> {
        return database.voteOnPost(currentUserId, tournamentId, postId, vote)
    }

    /**
     * get the username from an id
     * @param id the id
     * @return the username
     */
    fun getUsernameWithId(id: String): CompletableFuture<String> {
        return database.getUsername(id)
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

    /**
     * get the current user
     */
    fun getCurrentUser(): String {
        return currentUserId
    }

    private fun updateUserTournaments(firstClear: Boolean = false): CompletableFuture<Unit> {
        if (firstClear) {
            _userTournaments.clear()
            userTournaments.postValue(_userTournaments)
        }
        return database.getUserData(currentUserId).thenApplyAsync {
            it.tournaments?.let { it1 ->
                _userTournaments.clear()
                _userTournaments.addAll(it1)
                userTournaments.postValue(_userTournaments)
            }
        }
    }

    companion object {
        fun getFactory(database: Database, userId: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    TournamentModel(database, userId)
                }
            }
        }
    }
}
