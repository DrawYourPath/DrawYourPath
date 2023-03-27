package com.epfl.drawyourpath.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/**
 * Class that contains notifications related functions
 */
class NotificationsHelper(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Creates the notifications channels and activates the notifications.
     * Should be called when creating the MainActivity.
     */
    fun setupNotifications() {
        setupNotificationsChannels()
        //Starts the daily challenge reminders notifications
        RemindersManager.startReminder(context)
    }

    private fun setupNotificationsChannels() {
        //Setup challenges reminder channel:
        setupChannel(
            CHANNEL_CHALLENGES_REMINDER_ID,
            CHANNEL_CHALLENGES_REMINDER_NAME,
            CHANNEL_CHALLENGES_REMINDER_DESC,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        //Can setup other channels if needed (like social/friends, tournaments,...)
    }

    //Easy-to-use function to create notifications channels
    private fun setupChannel(id: String, name: String, descriptionText: String, importance: Int) {
        val channel = NotificationChannel(id, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }
}