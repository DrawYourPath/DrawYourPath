package com.epfl.drawyourpath.utils

import com.google.android.gms.maps.model.LatLng
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
}
