package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
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
class PathDrawingEndFragmentTest {
    private val mockPath = Path(listOf(listOf(LatLng(0.0, 0.0), LatLng(0.0, 1.0))))
    val date = LocalDate.of(2000, 1, 1).atTime(LocalTime.of(12, 0, 5)).toEpochSecond(ZoneOffset.UTC)
    private val mockRun = Run(path = mockPath, startTime = date, endTime = date + 75, duration = 75)
    private val context = ApplicationProvider.getApplicationContext<Context>()
    val expectedDistance = "111.32"
    val expectedSpeed = "1484.26"
    val expectedTime = "00:01:15"
    val expectedStartTime = "12:00:05"
    val expectedEndTime = "12:01:20"
    val expectedTimeFor1km = "00:00:00"

    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that clicking on the back to menu button show the main app menu
     */
    @Test
    fun checkThatClickingOnBackMenuButtonShowMainAppActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        intent.putExtra(PathDrawingActivity.EXTRA_COUNTDOWN_DURATION, 1L)
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)
        // wait that the countdown passed
        Thread.sleep(1001)
        // click on pause button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_pause_button))
            .perform(ViewActions.click())
        // click on stop button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_stop_button))
            .perform(ViewActions.click())
        // click on back to the menu button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_end_back_menu_button))
            .perform(ViewActions.click())
        // check that the main menu activity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.drawerLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        t.close()
    }

    /**
     * Test that the information displayed on the end drawing fragment are correct for the given run.
     */
    @Test
    fun checkInformationDisplayedOnEndDrawingFragment() {
        val scenario = launchFragmentInContainer<PathDrawingEndFragment>(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingEndFragment(run = mockRun)
        }
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the sports information displayed
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

        // check that the form recognized and the score are displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.formDescriptionPath))
            .check(ViewAssertions.matches(ViewMatchers.withSubstring(context.getString(R.string.shape_recognized_on_the_path_drawn))))
        Espresso.onView(ViewMatchers.withId(R.id.scorePath))
            .check(ViewAssertions.matches(ViewMatchers.withSubstring(context.getString(R.string.score_of_the_shape_recognized))))
        Espresso.onView(ViewMatchers.withId(R.id.descriptionTextFormDescription))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.ml_shape_recognition_description))))

        // check that the stop and resume buttons are displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_end_back_menu_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}
