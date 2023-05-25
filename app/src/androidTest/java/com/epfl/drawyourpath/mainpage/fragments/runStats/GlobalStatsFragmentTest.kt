package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.GlobalStatsFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GlobalStatsFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    /**
     * Check the score recognized and the score gives to this form are correctly displayed on this fragment.
     */
    @Test
    fun correctInfoGlobalStatsDisplayed() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                GlobalStatsFragment(
                    averageSpeed = 20.0,
                    averageDuration = 60.0,
                    averageDistance = 10.0,
                    totalDistanceGoal = 15.0,
                    totalActivityTimeGoal = 120.0,
                    totalPathNumberGoal = 5.0,
                )
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.global_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the speed displayed
        Espresso.onView(ViewMatchers.withId(R.id.average_speed_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.average_speed_in_m_s_title)} 20.0")))
        // check the duration displayed
        Espresso.onView(ViewMatchers.withId(R.id.average_duration_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.average_duration_in_s_title)} 00:01:00")))
        // check the distance displayed
        Espresso.onView(ViewMatchers.withId(R.id.average_distance_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.average_distance_in_km_title)} 0.01")))
        // check the distance goal displayed
        Espresso.onView(ViewMatchers.withId(R.id.distance_goal_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.number_of_distance_goal_reached)} 15")))
        // check the time goal displayed
        Espresso.onView(ViewMatchers.withId(R.id.time_goal_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.number_of_time_goal_reached)} 120")))
        // check the path goal displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_goal_global_stats))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.number_of_path_goal_reached)} 5")))
        scenario.close()
    }
}
