package com.epfl.drawyourpath.machineLearning

import com.epfl.drawyourpath.machineLearning.DigitalInk.downloadModelML
import com.epfl.drawyourpath.machineLearning.DigitalInk.recognizeDrawingML
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.Ink
import org.junit.Assert
import org.junit.Test

class DigitalInkTest {

    @Test
    fun downloadModelMLDoesNotThrow() {
        var model: DigitalInkRecognitionModel? = null
        try {
            model = downloadModelML().get()
            Assert.assertNotNull(model)
        } catch (e: Error) {
            Assert.assertTrue(false)
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
        val model = downloadModelML().get()

        val result = recognizeDrawingML(ink, model).get()

        Assert.assertNotNull(result)
        Assert.assertEquals("box", result.candidates[0].text)
        Assert.assertEquals(1.1F, result.candidates[0].score!!, 0.1F)
    }
}
