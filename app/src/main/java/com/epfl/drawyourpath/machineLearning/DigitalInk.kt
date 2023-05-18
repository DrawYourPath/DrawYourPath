package com.epfl.drawyourpath.machineLearning

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import java.util.concurrent.CompletableFuture

object DigitalInk {
    /**
     * Helper function to download an get the ML model
     * @return the ML model as a future of DigitalInkRecognitionModel
     */
    fun downloadModelML(
        modelIdentifier: DigitalInkRecognitionModelIdentifier = DigitalInkRecognitionModelIdentifier.AUTODRAW,
    ): CompletableFuture<DigitalInkRecognitionModel> {
        var result = CompletableFuture<DigitalInkRecognitionModel>()

        var model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        val remoteModelManager = RemoteModelManager.getInstance()
        remoteModelManager.isModelDownloaded(model)
            .addOnSuccessListener { is_downloaded: Boolean ->
                if (!is_downloaded) {
                    remoteModelManager.download(model, DownloadConditions.Builder().build())
                        .addOnSuccessListener {
                            Log.i("DigitalInk", "Model downloaded")
                            result.complete(model)
                        }
                        .addOnFailureListener { e: Exception ->
                            result.completeExceptionally(Error("Fail to download the model: ${e.localizedMessage}"))
                        }
                } else {
                    result.complete(model)
                }
            }
            .addOnFailureListener { e: Exception ->
                result.completeExceptionally(Error("Could not verify if the model was downloaded: ${e.localizedMessage}"))
            }
        return result
    }

    /**
     * Helper function to evaluate the drawing given the model
     * @param ink the drawing as an Ink
     * @param model the ML model
     * @return the result of the ML model as a future of MLDrawingResults, null if it could not classify
     */
    fun recognizeDrawingML(ink: Ink, model: DigitalInkRecognitionModel): CompletableFuture<RecognitionResult> {
        val recognizer: DigitalInkRecognizer =
            DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build())
        var classificationML = CompletableFuture<RecognitionResult>()
        recognizer.recognize(ink)
            .addOnSuccessListener { result: RecognitionResult ->
                classificationML.complete(result)
            }
            .addOnFailureListener { e: Exception ->
                classificationML.completeExceptionally(Error("Error while recognizing the model: ${e.localizedMessage}"))
            }
        return classificationML
    }
}
