package com.epfl.drawyourpath.utils

import com.epfl.drawyourpath.utils.Utils.coordinatesToBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.Ink
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun pointOrderIsConservedWhenConvertingCoordinates() {
        val c1 = LatLng(1.0, 1.0)
        val c2 = LatLng(2.0, 2.0)

        val p1 = Utils.coordinateToPoint(c1)
        val p2 = Utils.coordinateToPoint(c2)

        val ps = Utils.coordinatesToStroke(listOf(c1, c2))

        assertThat(ps.points, `is`(listOf(p1, p2)))
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
        coordinatesToBitmap(listOf(
            LatLng(1.0, 1.0),
            LatLng(2.0, 2.0),
            LatLng(3.0, 3.0),
        ))
    }
}
