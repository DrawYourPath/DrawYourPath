package com.epfl.drawyourpath.path

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.List

class RunTest {

    @Test(expected = IllegalArgumentException::class)
    fun constructorThrowsExceptionGivenWrongTimeStamps() {
        // create a run with invalid end time
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime - 1000 // end time is before start time

        Run(path, startTime, endTime)
    }

    @Test
    fun calculateDistance() {
        // create a run with a known path
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that distance is calculated correctly
        assertEquals(path.getDistance(), run.getDistance())
    }

    @Test
    fun calculateDuration() {
        // create a run with a known duration
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that duration is calculated correctly
        assertEquals(3000L, run.getDuration())
    }

    @Test
    fun calculateStartTime() {
        // create a run with a known timing
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that start time is handled correctly
        assertEquals(startTime, run.getStartTime())
    }

    @Test
    fun calculateDate() {
        // create a run with a known timing
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that date is calculated correctly
        val date = LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.UTC)
        assertEquals(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")), run.getDate())
    }

    @Test
    fun calculateEndTime() {
        // create a run with a known timing
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that end time is handled correctly
        assertEquals(endTime, run.getEndTime())
    }

    @Test
    fun calculateAverageSpeed() {
        // create a run with known path and duration
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(1.0, 1.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = 0L
        val endTime = 3000L
        val run = Run(path, startTime, endTime)

        assertEquals(path.getDistance() / 3000, run.getAverageSpeed(), 0.1)
    }

    @Test
    fun calculateCalories() {
        // create a run with known path and duration
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.1, 0.1)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)

        val met = 11.0
        // Speed should be 7 mph to have a MET of 11.0
        val speed = 7 / 2.23693629205440238
        // That means we need to run for (distance / speed) seconds
        val startTime = 0L
        val endTime = (path.getDistance() / speed).toLong()
        val run = Run(path, startTime, endTime)

        val cal = met * 70 * run.getDuration() / 3600
        assertEquals(cal, run.getCalories().toDouble(), cal / 50)
    }

    @Test
    fun calculateTimeForOneKilometer() {
        // create a run with known path and duration
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(1.0, 1.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = 0L
        val endTime = 3000L
        val run = Run(path, startTime, endTime)

        assertEquals(
            1000 / (path.getDistance() / 3000),
            run.getTimeForOneKilometer().toDouble(),
            1.0,
        )
    }

    @Test
    fun calculatePath() {
        // create a run with a known path
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.0, 1.0)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 3000
        val run = Run(path, startTime, endTime)
        // check that path is handled correctly
        assertEquals(2, run.getPath().size())
        assertEquals(points, run.getPath().getPoints())
        assertEquals(111319.9, path.getDistance(), 1.0)
        assertTrue(path.getPolyline() is List<PolylineOptions>)
        assertEquals(points, path.getPolyline()[0].points)
    }
}
