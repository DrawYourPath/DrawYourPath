package com.epfl.drawyourpath.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.login.ENABLE_ONETAP_SIGNIN
import com.epfl.drawyourpath.login.LoginActivity
import com.epfl.drawyourpath.login.RESTORE_USER_IN_KEYCHAIN
import com.epfl.drawyourpath.mainpage.USE_MOCK_CHALLENGE_REMINDER
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.CHALLENGES_REMINDER_ID
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.ERROR_REMINDER_ID
import com.epfl.drawyourpath.notifications.RemindersManager.Companion.REMINDER_KEY

/**
 * Class that can execute code when it receives an alarm (with onReceive())
 */
class AlarmReceiver : BroadcastReceiver() {

    /**
     * Sends a notification when an alarm is received
     */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val disableAutoReconnection = intent.getBooleanExtra(USE_MOCK_CHALLENGE_REMINDER, false)

        //Identify which notification to send, the intent is the one setup in the ReminderManager
        when (intent.getIntExtra(REMINDER_KEY, ERROR_REMINDER_ID)) {
            CHALLENGES_REMINDER_ID ->
                notificationManager.sendChallengeReminderNotification(
                    context,
                    !disableAutoReconnection
                )
        }

    }
}

/**
 * Sends a challenge reminder notification
 * @param applicationContext: the application context
 * @param enableAutoReconnection: set to true to allow immediate connection after clicking on notification
 */
fun NotificationManager.sendChallengeReminderNotification(
    applicationContext: Context, enableAutoReconnection: Boolean
) {
    // Intent to be launched when clicking on the notification
    val contentIntent = Intent(applicationContext, LoginActivity::class.java)
    contentIntent.putExtra(ENABLE_ONETAP_SIGNIN, enableAutoReconnection)
    contentIntent.putExtra(RESTORE_USER_IN_KEYCHAIN, enableAutoReconnection)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.resources.getString(R.string.channel_challenges_reminder_id)
    )
        .setContentTitle(applicationContext.resources.getString(R.string.challenges_reminder))
        .setContentText(applicationContext.resources.getString(R.string.challenge_reminder_notification_text))
        .setSmallIcon(R.drawable.ic_draw_path)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(applicationContext.resources.getString(R.string.challenge_reminder_notification_text))
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    // Activate the notification
    notify(0, builder.build())
}

