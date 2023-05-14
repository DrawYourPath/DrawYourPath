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
class PathDrawCurPerfFragTest {
    private val mockPath = Path(listOf(LatLng(0.0, 0.0), LatLng(0.0, 1.0)))
    val date = LocalDate.of(2000, 1, 1)
        .atTime(LocalTime.of(12, 0, 5))
        .toEpochSecond(ZoneOffset.UTC)
    private val mockRun = Run(path = mockPath, startTime = date, endTime = date + 75)
    val expectedDistance = "111.32"
    val expectedSpeed = "1484.26"
    val expectedTime = "00:01:15"

    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that the performance information displayed are correct.
     */
    @Test
    fun checkInformationDisplayedCurrentPerformance() {
        val scenario = launchFragmentInContainer<PathDrawingMainFragment>(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingMainFragment(run = mockRun, isDrawing = true)
        }
        // check that the performance are displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_current_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the time displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_time_current_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedTime)))
        // check the speed displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_speed_current_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedSpeed)))
        // check the distance displayed
        Espresso.onView(ViewMatchers.withId(R.id.display_distance_current_performance))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedDistance)))
        scenario.close()
    }
}
