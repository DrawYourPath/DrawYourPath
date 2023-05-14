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
        assertEquals(listOf(points), pathDrawingModel.pointsSection.getOrAwaitValue())
        assertEquals(listOf(points), pathDrawingModel.run.getOrAwaitValue().getPath().getPoints())
        assertEquals(listOf(points), pathDrawingModel.getRun().getPath().getPoints())
    }

    /**
     * Teest that a path with multiple section is correctyl created (so that we made multiple pause during the drawing)
     */
    @Test
    fun createPathWithMultipleSectionCorrectly() {
        val points = listOf(LatLng(46.52394457410412, 6.569600699830854), LatLng(46.52527331609736, 6.5719181283056285))
        val points2 = listOf(LatLng(47.52394457410412, 7.569600699830854), LatLng(47.52527331609736, 7.5719181283056285))
        val pathDrawingModel = PathDrawingModel()
        pathDrawingModel.setNewTimeInterval(0)
        pathDrawingModel.startRun()
        pathDrawingModel.updateRun(points[0])
        pathDrawingModel.updateRun(points[1])
        pathDrawingModel.pauseResumeRun()
        pathDrawingModel.pauseResumeRun()
        pathDrawingModel.updateRun(points2[0])
        pathDrawingModel.updateRun(points2[1])
        pathDrawingModel.pauseResumeRun()
        assertEquals(listOf(points, points2), pathDrawingModel.pointsSection.getOrAwaitValue())
        assertEquals(listOf(points, points2), pathDrawingModel.run.getOrAwaitValue().getPath().getPoints())
        assertEquals(listOf(points, points2), pathDrawingModel.getRun().getPath().getPoints())
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
