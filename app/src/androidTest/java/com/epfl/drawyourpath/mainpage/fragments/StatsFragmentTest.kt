package com.epfl.drawyourpath.mainpage.fragments

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.TableFromListFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StatsFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val averageSpeed = 20.0
    private val averageSpeedPerYear = mapOf(1.0 to 20.0, 2.0 to 30.0)
    private val averageSpeedPerMonth = mapOf(1.0 to 20.0, 2.0 to 30.0)
    private val averageDuration = 60.0
    private val averageDurationPerYear = mapOf(1.0 to 60.0, 2.0 to 70.0)
    private val averageDurationPerMonth = mapOf(1.0 to 60.0, 2.0 to 70.0)
    private val averageDistance = 10.0
    private val averageDistancePerYear = mapOf(1.0 to 10.0, 2.0 to 20.0)
    private val averageDistancePerMonth = mapOf(1.0 to 10.0, 2.0 to 20.0)
    private val totalDistanceGoal = 15.0
    private val totalDistanceGoalPerYear = mapOf(1.0 to 15.0, 2.0 to 25.0)
    private val totalActivityTimeGoal = 120.0
    private val totalActivityTimeGoalPerYear = mapOf(1.0 to 120.0, 2.0 to 130.0)
    private val totalPathNumberGoal = 5.0
    private val totalPathNumberGoalPerYear = mapOf(1.0 to 5.0, 2.0 to 15.0)

    private val mockStatsFragment = StatsFragment(
        averageSpeed = averageSpeed,
        averageSpeedPerYear = averageSpeedPerYear,
        averageSpeedPerMonth = averageSpeedPerMonth,
        averageDuration = averageDuration,
        averageDurationPerYear = averageDurationPerYear,
        averageDurationPerMonth = averageDurationPerMonth,
        averageDistance = averageDistance,
        averageDistancePerYear = averageDistancePerYear,
        averageDistancePerMonth = averageDistancePerMonth,
        totalDistanceGoal = totalDistanceGoal,
        totalDistanceGoalPerYear = totalDistanceGoalPerYear,
        totalActivityTimeGoal = totalActivityTimeGoal,
        totalActivityTimeGoalPerYear = totalActivityTimeGoalPerYear,
        totalPathNumberGoal = totalPathNumberGoal,
        totalPathNumberGoalPerYear = totalPathNumberGoalPerYear,
    )

    /**
     * Helper function to check that the global stats view is correctly displayed
     */
    private fun checkGlobalStatsView() {
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragment_stats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.global_stats))))
        // check that the global stats fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.global_stats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check texts displayed
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

        // check that the toggle button is not visible
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    /**
     * Helper function to check that the average speed view (with period year) is correctly displayed
     */
    private fun checkAverageSpeedYearView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Helper function to check that the average speed view (with period month) is correctly displayed
     */
    private fun checkAverageSpeedMonthView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_speed))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_speed_in_m_s))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
    }

    /**
     * Helper function to check that the average duration view (with period year) is correctly displayed
     */
    private fun checkAverageDurationYearView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_duration))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_duration_in_s))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Helper function to check that the average duration view (with period month) is correctly displayed
     */
    private fun checkAverageDurationMonthView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_duration))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per km is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_duration_in_s))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
    }

    /**
     * Helper function to check that the average distance view (with period year) is correctly displayed
     */
    private fun checkAverageDistanceYearView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_distance))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with distance per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_distance_in_m))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Helper function to check that the average distance view (with period month) is correctly displayed
     */
    private fun checkAverageDistanceMonthView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.average_distance))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with distance per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.average_distance_in_m))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.month))))
    }

    /**
     * Helper function to check that the distance goal view (with period year) is correctly displayed
     */
    private fun checkDistanceGoalView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.distance_goal))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with duration per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.distance_goal))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Helper function to check that the activity time goal view (with period year) is correctly displayed
     */
    private fun checkTimeGoalView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.activity_time_goal))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.activity_time_goal))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Helper function to check that the path number goal view (with period year) is correctly displayed
     */
    private fun checkPathNumberGoalView() {
        // check the title text displayed
        Espresso.onView(ViewMatchers.withId(R.id.statsTitleView))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.path_number_goal))))
        // check that the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check that the table with speed per segment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.path_number_goal))))
        // check the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.getString(R.string.year))))
    }

    /**
     * Check that the information's initially are correct(Global info stats displayed)
     */
    @Test
    fun initInfoDisplayedCorrectly() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        checkGlobalStatsView()
        scenario.close()
    }

    /**
     * Check that the information's displayed when we click on the arrow (in period year state) button on the right are corrected(check the transitions between the fragment views)
     * The correct transition is Global Stats, Average Speed(with year period), average Duration (with year period), average distance (with year period), total distance goal, total time goal,
     * number of path goal and global stats.
     */
    @Test
    fun correctTransitionWhenClickingOnRightArrowButtonWithYearPeriod() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragment_stats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Global stats displayed
        checkGlobalStatsView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER YEAR DISPLAYED
        checkAverageSpeedYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DURATION PER YEAR DISPLAYED
        checkAverageDurationYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DISTANCE PER YEAR DISPLAYED
        checkAverageDistanceYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // NUMBER PATH GOAL DISPLAYED
        checkPathNumberGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // Global stats displayed
        checkGlobalStatsView()

        scenario.close()
    }

    /**
     * Check that the information's displayed when we click on the arrow (in period month state) button on the right are corrected(check the transitions between the fragment views)
     * The correct transition is Global Stats, Average Speed(with year period), Average Speed(with month period), average Duration (with month period), average distance (with month period), total distance goal, total time goal,
     * number of path goal and global stats.
     */
    @Test
    fun correctTransitionWhenClickingOnRightArrowButtonWithMonthPeriod() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragment_stats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Global stats displayed
        checkGlobalStatsView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER YEAR DISPLAYED
        checkAverageSpeedYearView()

        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER MONTH DISPLAYED
        checkAverageSpeedMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DURATION PER MONTH DISPLAYED
        checkAverageDurationMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DISTANCE PER MONTH DISPLAYED
        checkAverageDistanceMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // NUMBER PATH GOAL DISPLAYED
        checkPathNumberGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // Global stats displayed
        checkGlobalStatsView()

        scenario.close()
    }

    /**
     * Check that the information's displayed (in period year state) when we click on the arrow button on the left are corrected(check the transitions between the fragment views)
     * The correct transition is Global Stats, number of path goal, total time goal, total distance goal, average distance (with year period), average Duration (with year period),
     * Average Speed(with year period) and global stats.
     */
    @Test
    fun correctTransitionWhenClickingOnLeftArrowButtonWithYearPeriod() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragment_stats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Global stats displayed
        checkGlobalStatsView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // NUMBER PATH GOAL DISPLAYED
        checkPathNumberGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DISTANCE PER YEAR DISPLAYED
        checkAverageDistanceYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DURATION PER YEAR DISPLAYED
        checkAverageDurationYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER YEAR DISPLAYED
        checkAverageSpeedYearView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // Global stats displayed
        checkGlobalStatsView()

        scenario.close()
    }

    /**
     * Check that the information's displayed (in period month state) when we click on the arrow button on the left are corrected(check the transitions between the fragment views)
     * The correct transition is Global Stats, number of path goal, total time goal, total distance goal, average distance (with year period), average distance (with month period),
     * average Duration (with month period), Average Speed(with month period) and global stats.
     */
    @Test
    fun correctTransitionWhenClickingOnLeftArrowButtonWithMonthPeriod() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragment_stats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Global stats displayed
        checkGlobalStatsView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // NUMBER PATH GOAL DISPLAYED
        checkPathNumberGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DISTANCE PER YEAR DISPLAYED
        checkAverageDistanceYearView()

        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // DISTANCE PER MONTH DISPLAYED
        checkAverageDistanceMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // DURATION PER MONTH DISPLAYED
        checkAverageDurationMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER MONTH DISPLAYED
        checkAverageSpeedMonthView()

        Espresso.onView(ViewMatchers.withId(R.id.changeLeftStats))
            .perform(ViewActions.click())

        // Global stats displayed
        checkGlobalStatsView()

        scenario.close()
    }

    /**
     * Test that the transition by clicking on the toggle button are correct in the average speed view.
     */
    @Test
    fun checkTransitionToggleAverageSpeed() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the average speed view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER YEAR DISPLAYED
        checkAverageSpeedYearView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER MONTH DISPLAYED
        checkAverageSpeedMonthView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE SPEED PER YEAR DISPLAYED
        checkAverageSpeedYearView()

        scenario.close()
    }

    /**
     * Test that the transition by clicking on the toggle button are correct in the average duration view.
     */
    @Test
    fun checkTransitionToggleAverageDuration() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the average duration view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // AVERAGE DURATION PER YEAR DISPLAYED
        checkAverageDurationYearView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE DURATION PER MONTH DISPLAYED
        checkAverageDurationMonthView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE DURATION PER YEAR DISPLAYED
        // check the title text displayed
        checkAverageDurationYearView()

        scenario.close()
    }

    /**
     * Test that the transition by clicking on the toggle button are correct in the average distance view.
     */
    @Test
    fun checkTransitionToggleAverageDistance() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the average distance view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // AVERAGE DISTANCE PER YEAR DISPLAYED
        checkAverageDistanceYearView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE DISTANCE PER MONTH DISPLAYED
        checkAverageDistanceMonthView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // AVERAGE DISTANCE PER YEAR DISPLAYED
        // check the title text displayed
        checkAverageDistanceYearView()

        scenario.close()
    }

    /**
     * Test that clicking on the toggle button does nothing in distance goal view.
     */
    @Test
    fun checkToggleBlockOnYearOnDistanceGoal() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the distance goal view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // DISTANCE GOAL DISPLAYED
        checkDistanceGoalView()

        scenario.close()
    }

    /**
     * Test that clicking on the toggle button does nothing in time goal view.
     */
    @Test
    fun checkToggleBlockOnYearOnTimeGoal() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the distance goal view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // TIME GOAL DISPLAYED
        checkTimeGoalView()

        scenario.close()
    }

    /**
     * Test that clicking on the toggle button does nothing in number path goal view.
     */
    @Test
    fun checkToggleBlockOnYearOnPathNumberGoal() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                mockStatsFragment
            }
        // access to the distance goal view
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeRightStats))
            .perform(ViewActions.click())

        // PATH NUMBER GOAL DISPLAYED
        checkPathNumberGoalView()

        // click on the toggle button
        Espresso.onView(ViewMatchers.withId(R.id.toggleStats))
            .perform(ViewActions.click())

        // PATH NUMBER GOAL DISPLAYED
        checkPathNumberGoalView()

        scenario.close()
    }
}
