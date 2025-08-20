package com.metraakladap.hexvault.ui.components

import android.graphics.Color
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

fun generateQrBitmap(content: String, size: Int = 640): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.TRANSPARENT)
        }
    }
    return bmp
}

@Composable
fun QrCode(modifier: Modifier = Modifier, content: String, size: Int = 640) {
    val bmp = generateQrBitmap(content, size)
    Image(bitmap = bmp.asImageBitmap(), contentDescription = null, modifier = modifier)
}


