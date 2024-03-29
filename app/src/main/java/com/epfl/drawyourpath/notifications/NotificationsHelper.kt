package com.epfl.drawyourpath.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.CHALLENGES_REMINDER_ID

/**
 * Class that contains notifications related functions
 */
class NotificationsHelper(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Creates the notifications channels and activates the notifications.
     * Should be called when creating the MainActivity.
     * @param useMockRemindersManager: set to true if you want to receive notification when main activity is launched
     */
    fun setupNotifications(useMockRemindersManager: Boolean) {
        setupNotificationsChannels()

        // Starts the daily challenge reminders notifications
        val remindersManager = if (useMockRemindersManager) {
            MockRemindersManager
        } else {
            DailyRemindersManager
        }
        val reminderTime = context.resources.getString(R.string.challenges_reminder_time)
        remindersManager.startReminder(context, reminderTime, CHALLENGES_REMINDER_ID)
    }

    private fun setupNotificationsChannels() {
        // Setup challenges reminder channel:
        setupChannel(
            context.resources.getString(R.string.channel_challenges_reminder_id),
            context.resources.getString(R.string.challenges_reminder),
            context.resources.getString(R.string.channel_challenges_reminder_desc),
            IMPORTANCE_DEFAULT,
        )
        // Can setup other channels if needed (like social/friends, tournaments,...)
    }

    // Easy-to-use function to create notifications channels
    private fun setupChannel(id: String, name: String, descriptionText: String, importance: Int) {
        val channel = NotificationChannel(id, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }
}
