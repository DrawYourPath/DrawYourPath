package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@RunWith(JUnit4::class)
class PathDrawingDetailPerformanceFragmentTest {
    private val mockPath = Path(listOf(listOf(LatLng(0.0, 0.0), LatLng(0.0, 1.0))))
    val date = LocalDate.of(2000, 1, 1).atTime(LocalTime.of(12, 0, 5)).toEpochSecond(ZoneOffset.UTC)
    private val mockRun = Run(path = mockPath, startTime = date, endTime = date + 75, duration = 75)
    val expectedDistance = "111.32"
    val expectedSpeed = "1484.26"
    val expectedTime = "00:01:15"
    val expectedStartTime = "12:00:05"
    val expectedEndTime = "12:01:20"
    val expectedTimeFor1km = "00:00:00"

    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that the performance information displayed are correct
     */
    @Test
    fun checkInformationDisplayedDetailPerformance() {
        val scenario = launchFragmentInContainer<PathDrawingMainFragment>(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingMainFragment(run = mockRun, isDrawing = false)
        }
        // check that the performance are displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_detail_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the time displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_time_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedTime)))
        // check the start time displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_start_time_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedStartTime)))
        // check the end time displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_end_time_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedEndTime)))
        // check the speed displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_speed_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedSpeed)))
        // check time taken for 1 km
        Espresso.onView(ViewMatchers.withId(R.id.display_time_km_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedTimeFor1km)))
        // check distance displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_distance_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedDistance)))
        // check calories displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_calories_detail_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(mockRun.getCalories().toString())))

        scenario.close()
    }
}
