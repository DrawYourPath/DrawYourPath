package com.epfl.drawyourpath.pathDrawing

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Duration
import kotlin.math.roundToInt

@RunWith(JUnit4::class)
class PathDrawingMainFragmentTest {
    private val mockPath = Path(listOf(LatLng(1.0, 1.0), LatLng(1.0, 0.0), LatLng(0.0, 0.0)))
    private val mockRun = Run(path = mockPath, startTime = 110L, endTime = 120L)

    /**
     * Test that the information displayed on the drawing fragment when the user is currently drawing are correct.
     */
    @Test
    fun checkInformationDisplayedInDrawingMode() {
        val scenario = launchFragmentInContainer<PathDrawingMainFragment>(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingMainFragment(run = mockRun, isDrawing = true)
        }
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //check that the sports information displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_current_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //check the time displayed
        val expectedTimeStr:String = displayedDuration(mockRun.getDuration())
        Espresso.onView(ViewMatchers.withId(R.id.display_time_current_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedTimeStr)))
        // check the speed displayed
        val expectedSpeed: String = ((mockRun.getAverageSpeed() * 100.0).roundToInt() / 100.0).toString()
        Espresso.onView(ViewMatchers.withId(R.id.display_speed_current_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedSpeed)))
        val expectedDistance: String = ((mockRun.getDistance() / 10.0).roundToInt() / 100.0).toString() // converted in km
        Espresso.onView(ViewMatchers.withId(R.id.display_distance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedDistance)))

        // check that the stop button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.pause_drawing_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    /**
     * Helper function to get string displayed dor a given duration
     * @param durationSec duration in seconds
     * @return the string that displayed the duration
     */
    private fun displayedDuration(durationSec: Long): String {
        val duration = Duration.ofSeconds(durationSec)
        val hours: Int = duration.toHours().toInt()
        val hoursStr: String = if (hours == 0) "00" else if (hours < 10) "0$hours" else hours.toString()
        val minutes: Int = duration.toMinutes().toInt() - hours * 60
        val minutesStr: String = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes.toString()
        val seconds: Int = duration.seconds.toInt() - 3600 * hours - 60 * minutes
        val secondsStr: String = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds.toString()

        return "$hoursStr:$minutesStr:$secondsStr"
    }
}
