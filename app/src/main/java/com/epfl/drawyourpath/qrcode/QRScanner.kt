package com.epfl.drawyourpath.qrcode

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import java.util.concurrent.CompletableFuture

const val REQUEST_CODE_QR_SCAN = 9933

class QRScanner {
    /**
     * Launches the scanner. If a scan is still being processed, the operation fails immediately.
     * @param activity the activity responsible for the scan.
     * @return a future completed when the user scanned a QR.
     */
    fun launchScanner(activity: Activity): CompletableFuture<String> {
        val operationResult = CompletableFuture<String>()

        if (onComplete != null) {
            operationResult.completeExceptionally(Exception("Operation still in progress."))
        }

        else {
            val integrator = IntentIntegrator(activity)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR code");
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.setRequestCode(REQUEST_CODE_QR_SCAN);
            integrator.initiateScan();

            onComplete = operationResult
        }
        return operationResult
    }

    /**
     * Method used to forward activity result to this class.
     * Must be called by the activity for it to work as expected.
     */
    fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            val scannedData = result.contents
            onComplete?.complete(scannedData)
        }
    }

    private var onComplete: CompletableFuture<String>? = null;
}