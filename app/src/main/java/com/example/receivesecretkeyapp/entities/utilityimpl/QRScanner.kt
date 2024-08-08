package com.example.receivesecretkeyapp.entities.utilityimpl

import androidx.activity.result.ActivityResultLauncher
import com.example.receivesecretkeyapp.activity.ActivityCapture
import com.journeyapps.barcodescanner.ScanOptions


class QRScanner(
    private val barLauncher: ActivityResultLauncher<ScanOptions>
) {
    companion object {
        const val EXTRA_CAMERA_FACING = "android.intent.extras.CAMERA_FACING"
    }

    fun startCamera() {
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(ActivityCapture::class.java)
        options.addExtra(EXTRA_CAMERA_FACING, 0)

        barLauncher.launch(options)
    }

    /*
    private fun captureScannerFrame(): Bitmap {
        // ViewGroup сканера
        val parentView = barcodeView.parent as View

        // Создание битмапа для хранения скриншота
        val bitmap = Bitmap.createBitmap(barcodeView.width, barcodeView.height, Bitmap.Config.ARGB_8888)

        // Создание холста для рисования
        val canvas = Canvas(bitmap)

        // Получение координат сканера на экране
        val location = IntArray(2)
        barcodeView.getLocationInWindow(location)

        // Получение скриншота
        parentView.draw(canvas)
        // Обрезка
        val x = location[0]
        val y = location[1]
        val width = barcodeView.width
        val height = barcodeView.height

        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }
    */


}