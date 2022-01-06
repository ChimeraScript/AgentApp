package com.app.todotoday.workmanager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.app.todotoday.MainActivity
import com.app.todotoday.R
import java.util.concurrent.TimeUnit

class NotificationWorker  (context: Context,
                           workerParams: WorkerParameters
): Worker(context, workerParams) {


    override fun doWork(): Result {

        val tittle = inputData.getString("tittle").toString()
        val description = inputData.getString("description").toString()
        val id = inputData.getInt("notificationId",0)


        notification(tittle,description,id)

        return Result.success()
    }


    companion object{
        fun saveNotification(context: Context, duration: Long, data: Data, tag: String){
            val notification = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(notification)
        }
    }




    private fun notification(tittle: String, description: String, notificationId: Int){

        // Create an explicit intent for an Activity in your app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val channelReminders =  R.string.channel_reminders.toString()

        val builder = NotificationCompat.Builder(applicationContext, channelReminders)
            .setSmallIcon(R.mipmap.bennetton)
            .setContentTitle(tittle)
            .setContentText(description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        // notificationId is a unique int for each notification that you must define
    }


}