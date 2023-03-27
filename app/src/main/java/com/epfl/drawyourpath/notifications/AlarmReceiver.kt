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

//Challenges reminder values
const val CHANNEL_CHALLENGES_REMINDER_NAME = "Challenges Reminder"
const val CHANNEL_CHALLENGES_REMINDER_DESC =
    "Notifications to remind you to complete your weekly challenges."
const val CHANNEL_CHALLENGES_REMINDER_ID = "CHALLENGES"
const val CHALLENGE_REMINDER_NOTIFICATION_ID = 0
const val CHALLENGE_REMINDER_NOTIFICATION_TEXT = "Have you completed all your challenges for today? If not, consider drawing more paths soon!"


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

        notificationManager.sendChallengeReminderNotification(context)
        // Send other time-based notifications here
    }
}

/**
 * Sends a challenge reminder notification
 * @param applicationContext: the application context
 */
fun NotificationManager.sendChallengeReminderNotification(
    applicationContext: Context,
) {
    // Intent to be launched when clicking on the notification
    val contentIntent = Intent(applicationContext, LoginActivity::class.java)
    contentIntent.putExtra(RESTORE_USER_IN_KEYCHAIN, true)
    contentIntent.putExtra(ENABLE_ONETAP_SIGNIN, true)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Build the notification
    val builder = NotificationCompat.Builder(applicationContext, CHANNEL_CHALLENGES_REMINDER_ID)
        .setContentTitle("Challenges Reminder")
        .setContentText(CHALLENGE_REMINDER_NOTIFICATION_TEXT)
        .setSmallIcon(R.drawable.ic_draw_path)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(CHALLENGE_REMINDER_NOTIFICATION_TEXT)
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    // Activate the notification
    notify(CHALLENGE_REMINDER_NOTIFICATION_ID, builder.build())
}

