package com.epfl.drawyourpath.utils


import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.Ink
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
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
    fun downloadModelMLDoesNotThrow() {
        var model: DigitalInkRecognitionModel? = null
        try {
            model = Utils.downloadModelML().get()
            assertNotNull(model)
        } catch (e: Error) {
            assertTrue(false)
        }
    }

    @Test
    fun modelRecognizesBox() {
        val sideLength = 0.001
        val baseLat = 46.5185
        val baseLng = 6.56177
        val c1 = LatLng(baseLat, baseLng)
        val c2 = LatLng(baseLat + sideLength, baseLng)
        val c3 = LatLng(baseLat + sideLength, baseLng + sideLength)
        val c4 = LatLng(baseLat, baseLng + sideLength)
        val c5 = LatLng(baseLat, baseLng)
        val stroke = Utils.coordinatesToStroke(listOf(c1, c2, c3, c4, c5))
        val ink = Ink.builder().addStroke(stroke).build()
        val model = Utils.downloadModelML().get()

        val result = Utils.recognizeDrawingML(ink, model).get()

        assertNotNull(result)
        assertEquals("box", result.classification)
        assertEquals(1.1F, result.rawScore, 0.1F)
    }
}
