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
}
