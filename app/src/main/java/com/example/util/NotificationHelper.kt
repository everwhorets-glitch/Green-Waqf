package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.Man1ClassDirectory
import com.example.data.model.MentoringScheduleCatalog

object NotificationHelper {
    const val CHANNEL_ID = "bank_bestari_schedule_channel"
    const val NOTIFICATION_ID = 1001
    const val NOTIFICATION_ID_H1 = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pengingat Setor Bank Bestari"
            val descriptionText = "Notifikasi pengingat harian dan H-1 jadwal penyetoran sampah 27 kelas MAN 1 Yogyakarta"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendHMinusOneReminder(
        context: Context,
        className: String,
        scheduleDay: String,
        mentorName: String
    ) {
        createNotificationChannel(context)
        val title = "🔔 H-1 Jadwal Setor Bank Bestari: Kelas $className"
        val body = "Besok ($scheduleDay) adalah jadwal setor sampah kelas $className! Kumpulkan botol, kardus, dan pilah sampah bersama teman kelasmu. Pendamping: $mentorName."
        dispatchNotification(context, NOTIFICATION_ID_H1, title, body)
    }

    fun sendDayOfReminder(
        context: Context,
        className: String,
        scheduleDay: String,
        mentorName: String
    ) {
        createNotificationChannel(context)
        val title = "🌿 Hari Ini Jadwal Setor: Kelas $className!"
        val body = "Hari ini ($scheduleDay) adalah giliran kelas $className menyetor ke Gazebo Bank Bestari! Tunjukkan QR akunmu ke admin saat penimbangan. Pendamping: $mentorName."
        dispatchNotification(context, NOTIFICATION_ID, title, body)
    }

    fun sendScheduleNotification(
        context: Context,
        targetClass: String,
        customMessage: String? = null
    ) {
        createNotificationChannel(context)

        val foundClass = Man1ClassDirectory.getClassByName(targetClass)
        val day = foundClass?.scheduleDay ?: "Senin"
        val upcomingMentoring = MentoringScheduleCatalog.getUpcomingScheduleForClass(targetClass)
        val mentor = upcomingMentoring?.mentors ?: "Bapak/Ibu Guru Pendamping Adiwiyata"

        val title = "🌿 Jadwal Setor Bank Bestari: Kelas $targetClass"
        val body = customMessage ?: "Hari $day adalah giliran kelas $targetClass menyetor ke Bank Bestari MAN 1 Yogyakarta. Pendamping: $mentor. Siapkan sampah terpilahmu!"

        dispatchNotification(context, NOTIFICATION_ID, title, body)
    }

    private fun dispatchNotification(
        context: Context,
        id: Int,
        title: String,
        body: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(id, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
