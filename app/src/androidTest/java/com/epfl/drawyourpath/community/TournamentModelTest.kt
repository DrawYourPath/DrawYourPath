package com.epfl.drawyourpath.community

import android.util.Log
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.epfl.drawyourpath.database.MockDatabase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
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

    /*@Test
    fun displayMockTournamentInStartingSoon() {
        val mockDatabase = MockDatabase()
        val tournamentModel = TournamentModel(mockDatabase, MockDatabase.mockUser.userId!!)
        waitUntilAllThreadAreDone()
        Log.d("test", tournamentModel.allTournamentIds.getOrAwaitValue().toString())
        Log.d("test", tournamentModel.allTournaments.getOrAwaitValue().toString())
        assertEquals(mockDatabase.tournaments.map { it.value }, tournamentModel.startingSoonTournament.getOrAwaitValue())
    }*/

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