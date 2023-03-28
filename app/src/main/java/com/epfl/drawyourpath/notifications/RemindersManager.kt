package com.epfl.drawyourpath.notifications

import android.content.Context

interface RemindersManager {

    /**
     * Use this function to choose when the notification reminding to complete the challenges should be displayed.
     *
     * @param context: the app context
     * @param reminderTime: the desired time at which the reminder notification should be displayed
     * @param reminderId: the id of the reminder (let default for challenge notification)
     */
    fun startReminder(context: Context, reminderTime: String = "16:00", reminderId: Int = 0)
}