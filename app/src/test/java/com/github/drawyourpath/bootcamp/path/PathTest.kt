package com.github.drawyourpath.bootcamp.path

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class PathTest {

    @Test
    fun `test creating empty path`() {
        val path = Path()
        assertEquals(0, path.size())
    }

    @Test
    fun `test creating path from list of points`() {
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val points = listOf(point1, point2)

        val path = Path(points)

        assertEquals(2, path.size())
        assertTrue(path.getPoints().containsAll(points))
    }

    @Test
    fun `addPoint adds a point to the path`() {
        val path = Path()
        path.addPoint(LatLng(1.0, 2.0))
        assertEquals(1, path.size())
    }

    @Test
    fun `getPoints returns an empty list for a new empty path`() {
        val path = Path()
        assertTrue(path.getPoints().isEmpty())
    }

    @Test
    fun `getPoints returns a list of all added points`() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPoint(point1)
        path.addPoint(point2)
        assertEquals(listOf(point1, point2), path.getPoints())
    }

    @Test
    fun `clear removes all points from the path`() {
        val path = Path()
        path.addPoint(LatLng(1.0, 2.0))
        path.clear()
        assertTrue(path.getPoints().isEmpty())
    }

    @Test
    fun `size returns the number of points in a non empty path`() {
        val path = Path()
        path.addPoint(LatLng(1.0, 2.0))
        path.addPoint(LatLng(3.0, 4.0))
        assertEquals(2, path.size())
    }


    @Test
    fun `getDistance returns the distance between added points`() {
        val path = Path()
        path.addPoint(LatLng(0.0, 0.0))
        path.addPoint(LatLng(0.0, 1.0))
        assertEquals(111319.9, path.getDistance(), 500.0)

    }


    @Test
    fun `getPolyline returns a PolylineOptions object`() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPoint(point1)
        path.addPoint(point2)
        val polyline = path.getPolyline()
        assertTrue(polyline is PolylineOptions)
        assertEquals(listOf(point1, point2), polyline.points)
    }

}