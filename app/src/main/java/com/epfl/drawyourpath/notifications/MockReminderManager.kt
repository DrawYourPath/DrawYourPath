package com.epfl.drawyourpath.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.epfl.drawyourpath.mainpage.USE_MOCK_CHALLENGE_REMINDER
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.REMINDER_KEY
import java.util.*

object MockRemindersManager : RemindersManager {

    override fun startReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.putExtra(USE_MOCK_CHALLENGE_REMINDER, true)
        //To identify which notification to send
        intent.putExtra(REMINDER_KEY, reminderId)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Sets the alarm
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(Calendar.getInstance().timeInMillis, pendingIntent),
            pendingIntent
        )
    }
}