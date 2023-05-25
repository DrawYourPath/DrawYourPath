package com.epfl.drawyourpath.community

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.database.MockDatabase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class TournamentModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var counting = CountingTaskExecutorRule()

    private val timeout: Long = 5

    private fun waitUntilAllThreadAreDone() {
        counting.drainTasks(timeout.toInt(), TimeUnit.SECONDS)
        Thread.sleep(100)
    }

    private fun waitForLiveDataToUpdate(tournamentModel: TournamentModel) {
        tournamentModel.startingSoonTournament.getOrAwaitValue()
        waitUntilAllThreadAreDone()
        tournamentModel.posts.getOrAwaitValue()
        waitUntilAllThreadAreDone()
        tournamentModel.startingSoonTournament.getOrAwaitValue()
        waitUntilAllThreadAreDone()
        tournamentModel.posts.getOrAwaitValue()
        waitUntilAllThreadAreDone()
        tournamentModel.startingSoonTournament.getOrAwaitValue()
        waitUntilAllThreadAreDone()
        tournamentModel.posts.getOrAwaitValue()
        waitUntilAllThreadAreDone()
    }

    @Test
    fun displayMockTournamentInStartingSoon() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)

        waitForLiveDataToUpdate(tournamentModel)

        // 1 tournament in your tournament
        assertEquals(mockDatabase.tournaments["0"]?.value!!.toString(), tournamentModel.yourTournament.getOrAwaitValue()[0].toString())
        // 2 tournaments in starting soon
        assertEquals(
            mockDatabase.tournaments.values.drop(1).map { it.value!!.toString() },
            tournamentModel.startingSoonTournament.getOrAwaitValue().map { it.first.toString() })
    }

    @Test
    fun tournamentBegunNotRegisteredIsInDiscover() {
        val mockDatabase = MockDatabase()
        val tournamentParameters = Tournament.TournamentParameters(
            "star wars",
            "star shape",
            LocalDateTime.now().minusDays(2L),
            LocalDateTime.now().plusDays(2L),
            Tournament.Visibility.PUBLIC
        )
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.createTournament(
            tournamentParameters, ApplicationProvider.getApplicationContext()
        ).get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // new tournament in discover
        assertEquals(
            mockDatabase.tournaments.values.find { it.value!!.name == "star wars" }!!.value!!.toString(),
            tournamentModel.discoverTournament.getOrAwaitValue()[0].toString()
        )
    }

    @Test
    fun tournamentAlreadyEndedNotInAnyTournamentList() {
        val mockDatabase = MockDatabase()
        val tournamentParameters = Tournament.TournamentParameters(
            "star wars",
            "star shape",
            LocalDateTime.now().minusDays(2L),
            LocalDateTime.now().minusDays(1L),
            Tournament.Visibility.PUBLIC
        )
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.createTournament(
            tournamentParameters, ApplicationProvider.getApplicationContext()
        ).get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // check in your tournament
        assertEquals(null, tournamentModel.yourTournament.getOrAwaitValue().find { it.name == "star wars" })
        // check in starting soon tournament
        assertEquals(null, tournamentModel.startingSoonTournament.getOrAwaitValue().find { it.first.name == "star wars" })
        // check in discover tournament
        assertEquals(null, tournamentModel.discoverTournament.getOrAwaitValue().find { it.name == "star wars" })
    }

    @Test
    fun displayOnlyPostOfMockTournament() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        // show post of mock tournament 0
        tournamentModel.showPostOf("0")

        waitForLiveDataToUpdate(tournamentModel)

        // posts of mock tournament
        assertEquals(mockDatabase.mockTournament.value!!.posts.toString(), tournamentModel.posts.getOrAwaitValue().toString())
    }

    @Test
    fun displayAllPostOfMockTournaments() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)

        waitForLiveDataToUpdate(tournamentModel)

        // all posts of registered tournament
        assertEquals(
            mockDatabase.tournaments.values.map { it.value!!.posts }.flatten().toString(),
            tournamentModel.posts.getOrAwaitValue().toString()
        )
    }

    @Test
    fun createdPostIsDisplayed() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.createPost(mockDatabase.mockTournament.value!!, MockDatabase.mockUser.runs!![0], ApplicationProvider.getApplicationContext()).get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // created post in posts
        assertEquals(
            mockDatabase.tournaments.values.map { it.value!!.posts }.flatten().toString(),
            tournamentModel.posts.getOrAwaitValue().toString()
        )
    }

    @Test
    fun unregisterStopShowingPostFromThisTournament() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.register("0", false).get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // no more posts
        assertEquals(listOf<TournamentPost>().toString(), tournamentModel.posts.getOrAwaitValue().toString())
    }

    @Test
    fun registerToTournamentShowItsPosts() {
        val mockDatabase = MockDatabase()
        val tournamentParameters = Tournament.TournamentParameters(
            "star wars",
            "star shape",
            LocalDateTime.now().minusDays(2L),
            LocalDateTime.now().plusDays(2L),
            Tournament.Visibility.PUBLIC
        )
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.createTournament(
            tournamentParameters, ApplicationProvider.getApplicationContext()
        ).get(2, TimeUnit.SECONDS)
        tournamentModel.register(mockDatabase.tournaments.values.find { it.value!!.name == "star wars" }!!.value!!.id, true)
        tournamentModel.createPost(
            mockDatabase.tournaments.values.find { it.value!!.name == "star wars" }!!.value!!,
            MockDatabase.mockUser.runs!![0],
            ApplicationProvider.getApplicationContext(),
        ).get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // new post in posts
        assertEquals(mockDatabase.tournaments.values.map { it.value!!.posts }.flatten().toString(),
            tournamentModel.posts.getOrAwaitValue().toString())
    }

    @Test
    fun addVoteDisplayNewVote() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.addVote(1, mockDatabase.mockPost.postId, "0").get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // updated vote in posts
        assertEquals(mockDatabase.tournaments.values.map { it.value!!.posts }.flatten().toString(),
            tournamentModel.posts.getOrAwaitValue().toString())
    }

    @Test
    fun removeVoteDisplayNewVote() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        tournamentModel.addVote(-1, mockDatabase.mockPost.postId, "0").get(2, TimeUnit.SECONDS)
        tournamentModel.addVote(0, mockDatabase.mockPost.postId, "0").get(2, TimeUnit.SECONDS)

        waitForLiveDataToUpdate(tournamentModel)

        // updated vote in posts
        assertEquals(mockDatabase.tournaments.values.map { it.value!!.posts }.flatten().toString(),
            tournamentModel.posts.getOrAwaitValue().toString())
    }

    /**
     * helper function to get result from live data
     */
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = timeout,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}