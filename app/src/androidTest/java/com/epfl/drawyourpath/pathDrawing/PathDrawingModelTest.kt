package com.epfl.drawyourpath.pathDrawing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(JUnit4::class)
class PathDrawingModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val timeout: Long = 5

    @Test
    fun addingPointAddsPointsToRun() {
        val points = listOf(LatLng(46.52394457410412, 6.569600699830854), LatLng(46.52527331609736, 6.5719181283056285))
        val pathDrawingModel = PathDrawingModel()
        pathDrawingModel.setNewTimeInterval(0)
        pathDrawingModel.startRun()
        pathDrawingModel.updateRun(points[0])
        pathDrawingModel.updateRun(points[1])
        pathDrawingModel.pauseResumeRun()
        assertEquals(points, pathDrawingModel.points.getOrAwaitValue())
        assertEquals(points, pathDrawingModel.run.getOrAwaitValue().getPath().getPoints())
        assertEquals(points, pathDrawingModel.getRun().getPath().getPoints())
    }

    @Test(expected = Error::class)
    fun pausingBeforeStartingThrowError() {
        PathDrawingModel().pauseResumeRun()
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
