package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.roundToInt

/**
 * This class is used to show the different fragments to draw a path(countdown, draw a path, see user performance, end view to return back to the menu)
 */
class PathDrawingActivity : AppCompatActivity() {
    //the user currently cached in the app
    private val userCached: UserModelCached by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // retrieve the userId from the main activity
        val userId = intent.getStringExtra(MainActivity.EXTRA_USER_ID)
        if (userId != null) {
            userCached.setCurrentUser(userId)
        } else {
            Toast.makeText(applicationContext, R.string.toast_test_error_message, Toast.LENGTH_LONG)
                .show()
        }
        setContentView(R.layout.activity_path_drawing_actvity)
        // lunch and display the countdown fragment
        val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.path_drawing_activity_content, PathDrawingCountDownFragment()).commit()
    }
}
/**
 * Get current date and time in epoch seconds
 * @return current date and time in epoch seconds
 */
fun getCurrentDateTimeInEpochSeconds(): Long {
    return LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
}

/**
 * Helper function to get a string to displayed a distance in kilometer
 * @param distance that we want to displayed in meters
 * @return the string that correspond to the distance in kilometers
 */
fun getStringDistance(distance: Double): String {
    // convert m to km
    val roundDistance: Double = (distance / 10.0).roundToInt() / 100.0
    return roundDistance.toString()
}

/**
 * Helper function to get a string to displayed a time duration in "hh:mm:ss"
 * @param time that we want to displayed in seconds
 * @return the string that correspond to the time in "hh:mm:ss"
 */
fun getStringDuration(time: Long): String {
    val duration = Duration.ofSeconds(time)
    val hours: Int = duration.toHours().toInt()
    val hoursStr: String = if (hours == 0) "00" else if (hours < 10) "0$hours" else hours.toString()
    val minutes: Int = duration.toMinutes().toInt() - hours * 60
    val minutesStr: String = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes.toString()
    val seconds: Int = duration.seconds.toInt() - 3600 * hours - 60 * minutes
    val secondsStr: String = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds.toString()

    return "$hoursStr:$minutesStr:$secondsStr"
}

/**
 * Helper function to get a string to displayed the start time and end time in "hh:mm:ss"
 * @param time that we want to displayed in seconds
 * @return the string that correspond to the time in "hh:mm:ss"
 */
fun getStringTimeStartEnd(time: Long): String {
    val localTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
    val hoursStr = localTime.hour.toString()
    val minutesStr = localTime.minute.toString()
    val secondsStr = localTime.second.toString()
    return "$hoursStr:$minutesStr:$secondsStr"
}

/**
 * Helper function to get a string to displayed a speed in m/s
 * @param speed that we want to displayed in m/s
 * @return the string that correspond to the speed in m/s
 */
fun getStringSpeed(speed: Double): String {
    val roundSpeed: Double = (speed * 100.0).roundToInt() / 100.0
    return roundSpeed.toString()
}
