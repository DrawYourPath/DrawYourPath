package com.epfl.drawyourpath.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object RemindersManager {

    /**
     * Use this function to choose when the notification reminding to complete the challenges should be displayed.
     *
     * @param context: the app context
     * @param reminderTime: the desired time at which the reminder notification should be displayed
     * @param reminderId: the id of the reminder (let default for challenge notification)
     */
    fun startReminder(
        context: Context,
        reminderTime: String = "16:00", //Default, could create a setting to change that, requires to cancel the previous reminder.
        reminderId: Int = 0
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val (hours, min) = reminderTime.split(":").map { it.toInt() }

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
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
