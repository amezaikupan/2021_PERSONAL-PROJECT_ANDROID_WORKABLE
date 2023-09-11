package com.improver.workable.Utils.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.improver.workable.Utils.sendNotification


class TaskReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager



        val receivedId = intent.getIntExtra("NotifID", 0)
        val recevedMessage = intent.getStringExtra("NotifName")

        notificationManager.sendNotification(
            receivedId,
            "$recevedMessage: Today is your task Deadline! Let's finish this!",
            context
        )

    }

}