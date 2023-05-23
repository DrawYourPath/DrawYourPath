package com.epfl.drawyourpath.utils

import com.epfl.drawyourpath.machineLearning.DigitalInk
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils.coordinatesToBitmap
import com.epfl.drawyourpath.utils.Utils.getBestRunRecognitionCandidate
import com.epfl.drawyourpath.utils.Utils.getBiggestPoint
import com.epfl.drawyourpath.utils.Utils.getSmallestPoint
import com.epfl.drawyourpath.utils.Utils.reducePath
import com.epfl.drawyourpath.utils.Utils.reduceSection
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.Ink
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class UtilsTest {

    @Test
    fun checkEmailReturnsExpectedResult() {
        assertFalse(Utils.checkEmail("Invalid"))
        assertTrue(Utils.checkEmail("valid@valid.org"))
    }

    @Test
    fun getCurrentDateTimeInEpochSecondsReturnsExpectedResult() {
        val prevTime = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val currTime = Utils.getCurrentDateTimeInEpochSeconds()
        assertTrue(currTime <= LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC))
        assertTrue(prevTime <= currTime)
    }

    @Test
    fun coordinateToInkReturnsExpectedValues() {
        val coordinate = LatLng(1.0, 1.0)

        val point = Utils.coordinateToPoint(coordinate)

        assertTrue(point.x < Math.PI)
        assertTrue(point.x > 0.0)

        assertTrue(point.y < Math.PI)
        assertTrue(point.y > 0.0)
    }

    @Test
    fun convertEmptyListOfCoordinatesReturnsEmptyStroke() {
        val stroke = Utils.coordinatesToStroke(emptyList())

        assertTrue(stroke.points.isEmpty())
    }

    @Test
    fun getBiggestPointReturnsTheBiggestPoint() {
        val biggest = getBiggestPoint(
            Ink.Stroke.builder().also {
                it.addPoint(Ink.Point.create(0f, 0f))
                it.addPoint(Ink.Point.create(10f, 0f))
                it.addPoint(Ink.Point.create(0f, 20f))
                it.addPoint(Ink.Point.create(5f, 5f))
            }.build(),
        )

        assertEquals(biggest.x, 10f)
        assertEquals(biggest.y, 20f)
    }

    @Test
    fun getSmallestPointReturnsTheSmallestPoint() {
        val smallest = getSmallestPoint(
            Ink.Stroke.builder().also {
                it.addPoint(Ink.Point.create(2f, 0f))
                it.addPoint(Ink.Point.create(10f, 2f))
                it.addPoint(Ink.Point.create(0f, 20f))
                it.addPoint(Ink.Point.create(5f, 5f))
            }.build(),
        )

        assertEquals(smallest.x, 0f)
        assertEquals(smallest.y, 0f)
    }

    @Test
    fun pointOrderIsConservedWhenConvertingCoordinates() {
        val c1 = LatLng(1.0, 1.0)
        val c2 = LatLng(2.0, 2.0)

        val p1 = Utils.coordinateToPoint(c1)
        val p2 = Utils.coordinateToPoint(c2)

        val ps = Utils.coordinatesToStroke(listOf(c1, c2))

        assertThat(ps.points, `is`(listOf(p1, p2)))
    }

    @Test
    fun getStringFromALL_CAPSConvertToString() {
        val ALL_CAPS = "THIS_IS_ALL_CAPS"
        val value = "This is all caps"
        assertThat(Utils.getStringFromALL_CAPS(ALL_CAPS), `is`(value))
    }

    @Test
    fun getALL_CAPSFromStringConvertToALL_CAPS() {
        val ALL_CAPS = "THIS_IS_ALL_CAPS"
        val value = "This is all caps"
        assertThat(Utils.getALL_CAPSFromString(value), `is`(ALL_CAPS))
    }

    @Test
    fun normalizedPointIsPlacedOnPadding() {
        val p1 = Ink.Point.create(2f, 2f)

        val res = Utils.normalizeStrokes(
            listOf(
                Ink.Stroke.builder().also {
                    it.addPoint(p1)
                }.build(),
            ),
            0.1f,
        )

        assertThat(res[0].points[0].x.toDouble(), `is`(closeTo(0.1, 0.001)))
        assertThat(res[0].points[0].y.toDouble(), `is`(closeTo(0.1, 0.001)))
    }

    @Test
    fun normalizedPointsMatchExpectedValues() {
        val p = listOf(
            Ink.Point.create(2f, 2f),
            Ink.Point.create(4f, 2f),
            Ink.Point.create(2f, 4f),
            Ink.Point.create(4f, 4f),
            Ink.Point.create(3f, 3f),
        )

        val expectedP = listOf(
            Ink.Point.create(0f, 0f),
            Ink.Point.create(1f, 0f),
            Ink.Point.create(0f, 1f),
            Ink.Point.create(1f, 1f),
            Ink.Point.create(0.5f, 0.5f),
        )

        val res = Utils.normalizeStrokes(
            listOf(
                Ink.Stroke.builder().also {
                    for (point in p) {
                        it.addPoint(point)
                    }
                }.build(),
            ),
            0.0f,
        )

        res[0].points.zip(expectedP).forEach {
            assertThat(it.first.x.toDouble(), `is`(closeTo(it.second.x.toDouble(), 0.001)))
            assertThat(it.first.y.toDouble(), `is`(closeTo(it.second.y.toDouble(), 0.001)))
        }
    }

    @Test
    fun coordinatesToBitmapDoesNotThrow() {
        coordinatesToBitmap(
            listOf(
                LatLng(1.0, 1.0),
                LatLng(2.0, 2.0),
                LatLng(3.0, 3.0),
            ),
        )
    }

    @Test
    fun testRecognizeTriangle() {
        val sideLength = 0.01
        val baseLat = 46.5185
        val baseLng = 6.56177
        // Draw square
        val c0 = LatLng(baseLat, baseLng)
        val c1 = LatLng(baseLat, baseLng + sideLength)
        val c2 = LatLng(baseLat + sideLength, baseLng + sideLength)

        val points = listOf(listOf(c0, c1, c2, c0))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val duration = 3000L
        val endTime = startTime + duration
        val run = Run(path, startTime, duration, endTime)

        val result = getBestRunRecognitionCandidate(run).get()

        assertNotNull(result)
        assertEquals("ARROW", result.text)
        assertEquals(0.64F, result.score!!, 0.1F)
    }

    @Test
    fun testRecognizeSquare() {
        val sideLength = 0.01
        val baseLat = 46.5185
        val baseLng = 6.56177
        // Draw square
        val c0 = LatLng(baseLat, baseLng)
        val c1 = LatLng(baseLat, baseLng + sideLength)
        val c2 = LatLng(baseLat + sideLength, baseLng + sideLength)
        val c3 = LatLng(baseLat + sideLength, baseLng)

        val points = listOf(listOf(c0, c1, c2, c3, c0))
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val duration = 3000L
        val endTime = startTime + duration
        val run = Run(path, startTime, duration, endTime)

        val result = getBestRunRecognitionCandidate(run).get()

        assertNotNull(result)
        assertEquals("ELLIPSE", result.text)
        assertEquals(0.0F, result.score!!, 0.1F)
    }

    @Test
    fun testReduceOnTrivialPath() {
        val sideLength = 0.001
        val baseLat = 46.5185
        val baseLng = 6.56177
        val c00 = LatLng(baseLat, baseLng)
        val c10 = LatLng(baseLat + sideLength, baseLng)
        val c11 = LatLng(baseLat + 2 * sideLength, baseLng)
        val c20 = LatLng(baseLat, baseLng + sideLength)
        val c21 = LatLng(baseLat + sideLength, baseLng + sideLength)

        val points = listOf(listOf(c00), listOf(c10, c11), listOf(c20, c21))
        val path = Path(points)
        val reducedPath = reducePath(path)

        assertEquals(points, reducedPath.getPoints())
    }

    @Test
    fun testReduceOnNoisyLinePath() {
        val sideLength = 0.001
        val noiseSize = sideLength / 1000
        val baseLat = 46.5185
        val baseLng = 6.56177
        val c00 = LatLng(baseLat, baseLng)
        val c01 = LatLng(baseLat, baseLng + sideLength + 2 * noiseSize)
        val c02 = LatLng(baseLat, baseLng + 2 * sideLength + noiseSize)
        val c03 = LatLng(baseLat, baseLng + 3 * sideLength - noiseSize)
        val c04 = LatLng(baseLat, baseLng + 4 * sideLength)

        val points = listOf(listOf(c00, c01, c02, c03, c04))
        val path = Path(points)
        val reducedPath = reducePath(path, 0.01F)

        assertEquals(listOf(listOf(c00, c04)), reducedPath.getPoints())
    }

    @Test
    fun testReduceOnNoisySquarePath() {
        val sideLength = 0.01
        val noiseSize = 4 * sideLength / 1000
        val baseLat = 46.5185
        val baseLng = 6.56177
        // Draw square
        var c00 = LatLng(baseLat, baseLng)
        var c01 = LatLng(baseLat, baseLng + sideLength * 1 / 3)
        var c02 = LatLng(baseLat, baseLng + sideLength * 2 / 3)
        var c10 = LatLng(baseLat, baseLng + sideLength)
        var c11 = LatLng(baseLat + sideLength * 1 / 2, baseLng + sideLength)
        var c20 = LatLng(baseLat + sideLength, baseLng + sideLength)
        var c30 = LatLng(baseLat + sideLength, baseLng)
        var c31 = LatLng(baseLat + sideLength * 2 / 3, baseLng)
        var c32 = LatLng(baseLat + sideLength * 1 / 3, baseLng)
        var c33 = LatLng(baseLat, baseLng)

        // Add noise
        c00 = LatLng(c00.latitude - 2 * noiseSize, c00.longitude - 1 * noiseSize)
        c01 = LatLng(c01.latitude, c01.longitude + 2 * noiseSize)
        c10 = LatLng(c10.latitude - 1 * noiseSize, c10.longitude + 3 * noiseSize)
        c20 = LatLng(c20.latitude + 1 * noiseSize, c20.longitude)
        c30 = LatLng(c30.latitude - 2 * noiseSize, c30.longitude + 1 * noiseSize)
        c32 = LatLng(c32.latitude + 2 * noiseSize, c32.longitude - 1 * noiseSize)

        val points = listOf(listOf(c00, c01, c02, c10, c11, c20, c30, c31, c32, c33))
        val path = Path(points)
        val reducedPath = reducePath(path, 0.01F)

        assertEquals(listOf(listOf(c00, c10, c20, c30, c33)), reducedPath.getPoints())
    }

    @Test
    fun testReduceOnNoisyLineSection() {
        val sideLength = 0.001
        val noiseSize = sideLength / 1000
        val baseLat = 46.5185
        val baseLng = 6.56177
        val c00 = LatLng(baseLat, baseLng)
        val c01 = LatLng(baseLat, baseLng + sideLength + 2 * noiseSize)
        val c02 = LatLng(baseLat, baseLng + 2 * sideLength + noiseSize)
        val c03 = LatLng(baseLat, baseLng + 3 * sideLength - noiseSize)
        val c04 = LatLng(baseLat, baseLng + 4 * sideLength)

        val points = listOf(c00, c01, c02, c03, c04)
        val epsilon = 4 * sideLength.toFloat() * 0.01F
        val reducedPath = reduceSection(points, epsilon)

        assertEquals(listOf(c00, c04), reducedPath)
    }
}
