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

    /**
     * registered tournaments by the user used to post to the livedata
     */
    private val _userTournaments: MutableList<String> = MockDatabase.mockUser.tournaments!!.toMutableList()

    /**
     * registered tournaments by the user
     */
    private val userTournaments: MutableLiveData<List<String>> = MutableLiveData(_userTournaments)

    /**
     * all tournaments id on the database
     */
    private val allTournamentIds = database.getAllTournamentsId()

    /**
     * map of all the tournament info
     */
    private val allTournamentInfo: MutableMap<String, LiveData<Tournament>> = mutableMapOf()

    /**
     * all tournaments in the database
     */
    private val allTournaments: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        // add a source for all tournaments id
        addSource(allTournamentIds) { tournamentIds ->
            tournamentIds.forEach { id ->
                // put in map only if it does not exist
                allTournamentInfo.getOrPut(id) {
                    database.getTournamentInfo(id).also { livedata ->
                        // add a source to all tournament info to listen to the changes of each tournament
                        addSource(livedata) { tournament ->
                            value = value?.filterNot { it.id == tournament.id }?.plusElement(tournament) ?: listOf(tournament)
                        }
                    }
                }
            }
            value = allTournamentInfo.values.mapNotNull { it.value }
        }
    }

    /**
     * your tournament is the list of registered tournament that are currently open
     */
    val yourTournament: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        // add a source to all the tournaments
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
        // add a source to the registered tournaments
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { ids.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
    }

    /**
     * starting soon tournament is the list of tournament that will open soon
     */
    val startingSoonTournament: LiveData<List<Pair<Tournament, Boolean>>> = MediatorLiveData<List<Pair<Tournament, Boolean>>>().apply {
        // add a source to all the tournaments
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { it.startDate > LocalDateTime.now() && it.endDate >= LocalDateTime.now() }.map { Pair(it, userTournaments.value!!.contains(it.id)) }
        }
        // add a source to the registered tournaments
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { it.startDate > LocalDateTime.now() && it.endDate >= LocalDateTime.now() }.map { Pair(it, ids.contains(it.id)) }
        }
    }

    /**
     * discover tournament is the list of unregistered tournament that are currently open
     */
    val discoverTournament: LiveData<List<Tournament>> = MediatorLiveData<List<Tournament>>().apply {
        // add a source to all the tournaments
        addSource(allTournaments) { tournaments ->
            value = tournaments.filter { !userTournaments.value!!.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
        // add a source to the registered tournaments
        addSource(userTournaments) { ids ->
            value = allTournaments.value!!.filter { !ids.contains(it.id) && it.startDate <= LocalDateTime.now() && it.endDate >= LocalDateTime.now() }
        }
    }

    /**
     * to know the post of which tournament to display (null = all tournaments)
     */
    private val postOf: MutableLiveData<String?> = MutableLiveData(null)

    /**
     * map of all posts by tournaments
     */
    private val allPosts: MutableMap<String, LiveData<List<TournamentPost>>> = mutableMapOf()

    /**
     * displayed posts of either all the registered tournaments or of some tournaments
     */
    val posts: LiveData<List<TournamentPost>> = postOf.switchMap { id ->
        // if postOf is set to a tournament then show its posts
        id?.let { database.getTournamentPosts(it) } ?: MediatorLiveData<List<TournamentPost>>().apply {
            // add a source to the registered tournaments
            addSource(userTournaments) { ids ->
                value = allPosts.filterKeys { ids.contains(it) }.values.mapNotNull { it.value }.flatten()
            }
            // add a source to all the tournaments id
            addSource(allTournamentIds) { tournamentIds ->
                tournamentIds.forEach { id ->
                    // put in map only if it does not exist
                    allPosts.getOrPut(id) {
                        database.getTournamentPosts(id).also { livedata ->
                            // add a source to all tournament posts to listen to the changes of each posts
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

    /**
     * update the user tournaments to get the new registered tournaments by the user
     * @param firstClear if set then clear the variables before getting the values
     */
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
        /**
         * get the factory of this model to choose the database and userId to use
         * @param database the database
         * @param userId the user id
         * @return the corresponding factory
         */
        fun getFactory(database: Database, userId: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    TournamentModel(database, userId)
                }
            }
        }
    }
}
