package com.epfl.drawyourpath.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.REMINDER_KEY
import java.util.*

object DailyRemindersManager : RemindersManager {

    override fun startReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val (hours, min) = reminderTime.split(":").map { it.toInt() }

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                //Put this extra to be able to identify which notification should be send when the alarm is triggered
                intent.putExtra(REMINDER_KEY, reminderId)
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        //Sets the calendar that contain the reminder time
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
        }

        //Avoids launching the alarm when the desired time is in the past compared to current time
        val currentTime =
            Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.MINUTE, 1) }.timeInMillis
        if (currentTime - calendar.timeInMillis > 0) {
            calendar.add(Calendar.DATE, 1)
        }

        //Sets the alarm
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }
}
