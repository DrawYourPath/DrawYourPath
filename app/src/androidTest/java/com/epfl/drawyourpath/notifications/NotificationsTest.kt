package com.epfl.drawyourpath.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.USE_MOCK_CHALLENGE_REMINDER
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NotificationsTest {
    private val uiDevice by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    @Before
    fun clearCreatedNotifications() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
    }

    @Test
    fun launchingMainActivitySchedulesAlarm() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Check that no alarm is scheduled
        assertTrue(alarmManager.nextAlarmClock == null)

        val intent = Intent(
            context,
            MainActivity::class.java
        )
        val scenario: ActivityScenario<MainActivity> = ActivityScenario.launch(intent)

        //Check that an alarm is scheduled
        assertTrue(alarmManager.canScheduleExactAlarms())
        assertTrue(alarmManager.nextAlarmClock != null)

        scenario.close()
    }

    @Test
    fun challengeReminderNotificationIsDisplayedWhenAlarmIsTriggered() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        //Launch the intent with mock alarm, so will trigger notification "instantly"
        val intent = Intent(
            context,
            MainActivity::class.java
        )
        intent.putExtra(USE_MOCK_CHALLENGE_REMINDER, true)
        val scenario: ActivityScenario<MainActivity> = ActivityScenario.launch(intent)

        uiDevice.pressHome()
        uiDevice.openNotification()
        //If no timeout, a notification has arrived
        uiDevice.wait(
            Until.hasObject(By.textStartsWith(context.resources.getString(R.string.app_name))),
            30000
        )

        //Check that the title is the expected one
        val expectedTitle =
            context.resources.getString(R.string.challenge_reminder_notification_title)
        val title = uiDevice.findObject(By.text(expectedTitle))
        assertEquals(expectedTitle, title.text)

        //Check that the text is the expected one
        val expectedText =
            context.resources.getString(R.string.challenge_reminder_notification_text)
        val text = uiDevice.findObject(By.textStartsWith(expectedText))
        assertTrue(text.text.startsWith(expectedText))

        //Close notifications
        uiDevice.pressBack()
        scenario.close()
    }

    @Test
    fun clickingOnNotificationLaunchesLoginActivity() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()

        //Launch the intent with mock alarm, so will trigger notification "instantly"
        val intent = Intent(
            context,
            MainActivity::class.java
        )
        intent.putExtra(USE_MOCK_CHALLENGE_REMINDER, true)
        val scenario: ActivityScenario<MainActivity> = ActivityScenario.launch(intent)

        uiDevice.pressHome()
        uiDevice.openNotification()
        //If no timeout, a notification has arrived
        uiDevice.wait(
            Until.hasObject(By.textStartsWith(context.resources.getString(R.string.app_name))),
            30000
        )

        //Click on expected notification
        val expectedTitle =
            context.resources.getString(R.string.challenge_reminder_notification_title)
        val title = uiDevice.findObject(By.text(expectedTitle))
        title.click()

        //Check that the login page is displayed
        onView(withId(R.id.TXT_Title)).check(matches(isDisplayed()))

        Intents.release()
        scenario.close()
    }
}