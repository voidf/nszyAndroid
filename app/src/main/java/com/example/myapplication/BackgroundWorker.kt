package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.myapplication.ui.transform.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class BackgroundWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params){
    override fun doWork(): Result {
        val appctx = applicationContext
        return try {
            Log.d("[Worker]", "doing work!")
            runBlocking {
                val lib = Json{ignoreUnknownKeys=true}.decodeFromString<GeoapiResponse>(
                    getgeoapi()
                ).location[0].id

                val wea = decodenow(getnow(lib))

                val cid = "天气"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "weatherChannel"
                    val descriptionText = "weatherChannelDesc"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(cid, name, importance).apply {
                        description = descriptionText
                    }
                    // Register the channel with the system
                    val notificationManager: NotificationManager =
                        appctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                var notification_bdr = NotificationCompat.Builder(appctx, cid)
                    .setSmallIcon(R.drawable._100)
                    .setContentText("${cur_loc.name}：${wea.now.text}，${wea.now.temp}°")
                    .setContentTitle("${cur_loc.name}的天气推送")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                var noti = notification_bdr.build()
                with(NotificationManagerCompat.from(appctx)){
                    val notificationManager: NotificationManager =
                        appctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, noti)
                }
            }
            Log.d("[Worker]", "done work!")
            Result.success()
        } catch (thr: Throwable)
        {
            Result.failure()
        }
    }
}