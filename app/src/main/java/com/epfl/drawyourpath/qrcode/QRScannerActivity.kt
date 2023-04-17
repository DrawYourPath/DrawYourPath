package com.epfl.drawyourpath.qrcode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.epfl.drawyourpath.R
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.camera.CameraSettings

const val SCANNER_ACTIVITY_RESULT_CODE = 9933
const val SCANNER_ACTIVITY_RESULT_KEY = "result"

fun launchFriendQRScanner(activity: Activity, reqCode: Int) {
    val intent = Intent(activity, QRScannerActivity::class.java)
    startActivityForResult(activity, intent, reqCode, null)
}

class QRScannerActivity : AppCompatActivity(R.layout.activity_scan_friend_qr) {
    private lateinit var scannerView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = findViewById(R.id.SV_Scanner)

        findViewById<Button>(R.id.BT_Cancel).setOnClickListener { onCancelClicked() }

        setupCamera()
    }

    private fun onCancelClicked() {
        completeWithResult(null)
    }

    private fun setupCamera() {
        val settings = CameraSettings()
        settings.requestedCameraId = 0
        scannerView.cameraSettings = settings
        scannerView.setStatusText("Place a friend's QR inside the rectangle to scan it.")
        scannerView.resume()
        scannerView.decodeSingle {
            completeWithResult(it.text)
        }
    }

    private fun completeWithResult(data: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra(SCANNER_ACTIVITY_RESULT_KEY, data)
        setResult(SCANNER_ACTIVITY_RESULT_CODE, resultIntent)
        finish()
    }
}