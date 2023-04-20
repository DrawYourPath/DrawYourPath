package com.epfl.drawyourpath.qrcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Generates a QRCode bitmap image.
 * @param data The data encoded inside the QRCode.
 * @param size The size of the QR code.
 * @return A Bitmap object containing the QRCode.
 */
fun generateQR(data: String, size: Int): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
    for (x in 0 until bitMatrix.width) {
        for (y in 0 until bitMatrix.height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) -0x1000000 else -0x1)
        }
    }
    return bitmap
}
