package com.epfl.drawyourpath.mainpage.fragments.runStats

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
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.pathDrawing.PathDrawingActivity
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@RunWith(JUnit4::class)
class RunInfoStatsFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val mockPath = Path(listOf(listOf(LatLng(0.0, 0.0), LatLng(0.0, 1.0))))
    private val date = LocalDate.of(2000, 1, 1).atTime(LocalTime.of(12, 0, 5)).toEpochSecond(ZoneOffset.UTC)
    private val mockRun = Run(path = mockPath, startTime = date, endTime = date + 75, duration = 75)

    /**
     * Test that clicking on a run displayed more info on this run in run info stats fragment
     */
    @Test
    fun runInfoDisplayedOnClick() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

        // Go to history
        Espresso.onView(ViewMatchers.withId(R.id.history_menu_item)).perform(ViewActions.click())

        // click on a run
        Espresso.onView(ViewMatchers.withId(R.id.run_item_layout)).perform(ViewActions.click())

        // Check fragment the fragment the displays run info is displayed
        Espresso.onView(ViewMatchers.withId(R.id.run_info_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        t.close()
    }

    /**
     * Check that the information's initially are correct(Path drawn informations displayed)
     */
    @Test
    fun initInfoDisplayedCorrectly() {
        val scenario =
            launchFragmentInContainer<RunInfoStatsFragment>(themeResId = R.style.Theme_Bootcamp) {
                RunInfoStatsFragment(run = mockRun)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.run_info_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_drawn))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the description of the form and the score is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    /**
     * Check that the information's displayed when we click on the arrow button on the right are corrected(check the transitions between the fragment views)
     * The correct transition is PATH_DRAWN, GLOBAL_STATS, AVERAGE_SPEED_KM, DURATION_KM, DISTANCE_SEGMENT, DURATION_SEGMENT, AVERAGE_SPEED_SEGMENT
     */
    @Test
    fun correctTransitionWhenClickingOnRightArrowButton() {
        val scenario =
            launchFragmentInContainer<RunInfoStatsFragment>(themeResId = R.style.Theme_Bootcamp) {
                RunInfoStatsFragment(run = mockRun)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.run_info_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // PATH DRAWN DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_drawn))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the description of the form and the score is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // GLOBAL STATS DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.global_stats))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the global stats table is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_detail_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // AVERAGE SPEED PER KM DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed_per_km))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_km))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // DURATION PER KM DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.duration_per_km))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_km))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.duration_in_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // DISTANCE PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.distance_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with distance per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_m))))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // DURATION PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.duration_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.duration_in_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // AVERAGE SPEED PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeRightRunInfo))
            .perform(ViewActions.click())
        // PATH DRAWN DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_drawn))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the description of the form/score is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    /**
     * Check that the information's displayed when we click on the arrow button on the left are corrected(check the transitions between the fragment views)
     * The correct transition is PATH_DRAWN, AVERAGE_SPEED_SEGMENT, DURATION_SEGMENT, DISTANCE_SEGMENT, DURATION_KM, AVERAGE_SPEED_KM, GLOBAL_STATS
     */
    @Test
    fun correctTransitionWhenClickingOnLeftArrowButton() {
        val scenario =
            launchFragmentInContainer<RunInfoStatsFragment>(themeResId = R.style.Theme_Bootcamp) {
                RunInfoStatsFragment(run = mockRun)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.run_info_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // PATH DRAWN DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_drawn))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the description of the form and the score is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // AVERAGE SPEED PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // DURATION PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.duration_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.duration_in_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // DISTANCE PER SEGMENT DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.distance_per_segment))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with distance per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.segment))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_m))))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // DURATION PER KM DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.duration_per_km))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_km))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.duration_in_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // AVERAGE SPEED PER KM DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed_per_km))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_in_km))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // GLOBAL STATS DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.global_stats))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the global stats table is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_detail_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftRunInfo))
            .perform(ViewActions.click())
        // PATH DRAWN DISPLAYED
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.titleRunInfo))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_drawn))))
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the description of the form/score is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}
