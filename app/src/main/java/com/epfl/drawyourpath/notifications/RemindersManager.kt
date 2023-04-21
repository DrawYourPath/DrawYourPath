package com.epfl.drawyourpath.notifications

import android.content.Context

interface RemindersManager {
    companion object {
        // Key for reminder intents' extras
        const val REMINDER_KEY = "Reminder"

        // Reminder Ids for time-based notifications
        const val ERROR_REMINDER_ID = -1
        const val CHALLENGES_REMINDER_ID = 0
    }

    /**
     * Use this function to choose when the reminding notifications should be displayed.
     *
     * @param context: the app context
     * @param reminderTime: the desired time at which the reminder notification should be displayed
     * @param reminderId: the id of the reminder, use different id for different reminders
     */
    fun startReminder(context: Context, reminderTime: String, reminderId: Int)
}
