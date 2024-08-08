package com.example.receivesecretkeyapp.entities.utilityimpl

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.example.receivesecretkeyapp.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

class QREncoder(context: Context) {

    private val qrColor = ContextCompat.getColor(context, R.color.black)
    private val transparent = ContextCompat.getColor(context, R.color.white)

    @Throws(WriterException::class)
    fun encodeAsBitmap(contentsToEncode: String, size: Int): Bitmap {
        val hints = HashMap<EncodeHintType, Any>().apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }
        val result: BitMatrix = MultiFormatWriter().encode(
            contentsToEncode,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

        val pixels = IntArray(size * size) { index ->
            val x = index % size
            val y = index / size
            if (result[x, y]) qrColor else transparent
        }

        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }

}