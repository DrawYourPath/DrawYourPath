package com.epfl.drawyourpath.utils

import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilsTest {

    @Test
    fun coordinateToInkReturnsExpectedValues() {
        val coordinate = LatLng(1.0, 1.0)

        val point = Utils.coordinateToInk(coordinate)

        assertTrue(point.x < Math.PI)
        assertTrue(point.x > 0.0)

        assertTrue(point.y < Math.PI)
        assertTrue(point.y > 0.0)
    }

    @Test
    fun convertEmptyListOfCoordinatesReturnsEmptyStroke() {
        val stroke = Utils.coordinatesToInk(emptyList())

        assertTrue(stroke.points.isEmpty())
    }

    @Test
    fun pointOrderIsConservedWhenConvertingCoordinates() {
        val c1 = LatLng(1.0, 1.0)
        val c2 = LatLng(2.0, 2.0)

        val p1 = Utils.coordinateToInk(c1)
        val p2 = Utils.coordinateToInk(c2)

        val ps = Utils.coordinatesToInk(listOf(c1, c2))

        assertThat(ps.points, `is`(listOf(p1, p2)))
    }
}