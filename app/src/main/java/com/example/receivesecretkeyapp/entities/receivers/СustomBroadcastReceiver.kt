package com.example.receivesecretkeyapp.entities.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.receivesecretkeyapp.entities.interfaces.BroadcastReceiverListener

class CustomBroadcastReceiver : BroadcastReceiver() {

    private var listener: BroadcastReceiverListener? = null

    fun setListener(listener: BroadcastReceiverListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ru.shalkoff.vsu_lesson2_2024.SURF_ACTION") {
            val message = intent.getStringExtra("message")

            listener?.onBroadcastReceived(message ?: "")
        }
    }
}