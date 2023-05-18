package com.epfl.drawyourpath.utils

import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.utils.Utils.reducePath
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
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
    fun testReduceOnLinePath() {
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
}
