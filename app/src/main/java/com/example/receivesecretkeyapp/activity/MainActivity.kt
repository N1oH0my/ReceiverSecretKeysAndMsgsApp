package com.example.receivesecretkeyapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.receivesecretkeyapp.R
import com.example.receivesecretkeyapp.databinding.ActivityMainBinding
import com.example.receivesecretkeyapp.entities.receivers.CustomBroadcastReceiver
import com.example.receivesecretkeyapp.entities.interfaces.BroadcastReceiverListener
import com.example.receivesecretkeyapp.entities.receivers.ImageContentResolver
import com.example.receivesecretkeyapp.entities.utilityimpl.QREncoder
import com.example.receivesecretkeyapp.entities.utilityimpl.QRScanner
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.io.File

class MainActivity : AppCompatActivity(), BroadcastReceiverListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var broadcastReceiver: CustomBroadcastReceiver
    private lateinit var imageContentResolverClient: ImageContentResolver
    private lateinit var qrScanner: QRScanner
    private lateinit var qrEncoder: QREncoder
    private lateinit var barLauncher: ActivityResultLauncher<ScanOptions>

    private var lastSecretKeyMessage: String? = null
    private var lastBroadcastMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        broadcastReceiver = CustomBroadcastReceiver()
        val filter = IntentFilter("ru.shalkoff.vsu_lesson2_2024.SURF_ACTION")
        broadcastReceiver.setListener(this)
        registerReceiver(broadcastReceiver, filter)

        val REQUEST_CAMERA = 1
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }

        initResolvers()
        initQRScanner()
        initQREncoder()
        initListeners()

    }

    private fun onQRScanCompleted(data: String) {
        runOnUiThread {
            binding.urlTextView.text = data
        }
    }

    private fun initQRScanner() {
        barLauncher = registerForActivityResult(
            ScanContract()
        ) { result ->
            if (result.contents != null) {
                runOnUiThread {
                    onQRScanCompleted(result.contents.toString())

                    val bitmap = qrEncoder.encodeAsBitmap(result.contents.toString(), 600)
                    val imageView = binding.qrImageView
                    imageView.setImageBitmap(bitmap)
                }

            }
        }
        qrScanner = QRScanner(barLauncher)
    }

    private fun initQREncoder() {
        qrEncoder = QREncoder(this)
    }

    private fun initListeners() {
        binding.idButtonSecretKey.setOnClickListener {
            getSecretKey()
        }
        binding.getLastImgButton.setOnClickListener {
            val imageUri = imageContentResolverClient.queryImage()
            imageUri?.let {
                val imageView: ImageView = binding.lastImgImageView
                Glide.with(this)
                    .load(it)
                    .into(imageView)

                binding.urlTextView.text = it.toString()
            }
        }
        binding.startScanBtn.setOnClickListener {
            qrScanner.startCamera()
        }
    }

    private fun initResolvers() {
        imageContentResolverClient = ImageContentResolver(this)
    }

    @SuppressLint("Range")
    private fun getSecretKey(){
        val contentResolver = contentResolver
        val uri = Uri.parse("content://dev.surf.android.provider/text")
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val text = it.getString(it.getColumnIndex("text"))
                lastSecretKeyMessage = text

                makeToast("Received secret key: $text")
                makeLog("MainActivityMessages", " ")
                makeLog("MainActivityMessages", "Received new secret key message changed to $lastSecretKeyMessage")
            }
            else{
                makeToast("no key")
            }
            cursor.close()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        makeLog("MainActivityMessages", " ")
        makeLog("MainActivityMessages", "Saving messages...")

        outState.putString("lastSecretKeyMessage", lastSecretKeyMessage)
        outState.putString("lastBroadcastMessage", lastBroadcastMessage)
        super.onSaveInstanceState(outState)

        makeLog("MainActivityMessages", "Saved secret key message: $lastSecretKeyMessage")
        makeLog("MainActivityMessages", "Saved broadcast message: $lastBroadcastMessage")
    }

    override fun onRestoreInstanceState(restoreState: Bundle){
        makeLog("MainActivityMessages", " ")
        makeLog("MainActivityMessages", "Restoring messages...")

        super.onRestoreInstanceState(restoreState)
        lastSecretKeyMessage = restoreState.getString("lastSecretKeyMessage")
        lastBroadcastMessage = restoreState.getString("lastBroadcastMessage")

        makeLog("MainActivityMessages", "Last secret key message restored: $lastSecretKeyMessage")
        makeLog("MainActivityMessages", "Last received broadcast message restored: $lastBroadcastMessage")
    }

    override fun onBroadcastReceived(message: String) {
        lastBroadcastMessage = message

        makeToast("Custom received message: $message")
        makeLog("MainActivityMessages", " ")
        makeLog("MainActivityMessages", "Received new broadcast message: $message")
    }


    private fun makeToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun makeLog(tag: String, message: String){
        Log.d(tag, message)
    }
}