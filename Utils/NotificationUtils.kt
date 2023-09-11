package com.improver.workable.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.improver.workable.MainActivity
import com.improver.workable.R

private val GROUP_KEY_TASK = "com.android.example.TASK_DEADLINE"


fun NotificationManager.sendNotification(
    notificationID: Int,
    messageBody: String,
    applicationContext: Context
) {


    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(applicationContext.getString(R.string.notif_deadline))
        .setContentText(messageBody)
        .setGroup(GROUP_KEY_TASK)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Deadline Notification"
        val descriptionText = "Deadline Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel((R.string.channel_id).toString(), name, importance).apply {
            description = descriptionText
        }
        this.createNotificationChannel(channel)
    }

    notify(notificationID, builder.build())



    
}


private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library

}


fun NotificationManager.cancelNotification(id: Int) {
    cancel(id)
//    cancelAll()
}
//
//private object NotificationID {
//    private val c: AtomicInteger = AtomicInteger(0)
//    val iD: Int
//        get() = c.incrementAndGet()
//}