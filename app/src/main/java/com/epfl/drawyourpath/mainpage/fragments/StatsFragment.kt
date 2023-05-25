package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.Statistics.getAverageDistance
import com.epfl.drawyourpath.challenge.Statistics.getAverageDistancePerMonth
import com.epfl.drawyourpath.challenge.Statistics.getAverageDistancePerYear
import com.epfl.drawyourpath.challenge.Statistics.getAverageDuration
import com.epfl.drawyourpath.challenge.Statistics.getAverageDurationPerMonth
import com.epfl.drawyourpath.challenge.Statistics.getAverageDurationPerYear
import com.epfl.drawyourpath.challenge.Statistics.getAverageSpeed
import com.epfl.drawyourpath.challenge.Statistics.getAverageSpeedPerMonth
import com.epfl.drawyourpath.challenge.Statistics.getAverageSpeedPerYear
import com.epfl.drawyourpath.challenge.Statistics.getDistancePerYear
import com.epfl.drawyourpath.challenge.Statistics.getShapeDrawnCount
import com.epfl.drawyourpath.challenge.Statistics.getShapeDrawnCountPerYear
import com.epfl.drawyourpath.challenge.Statistics.getTimePerYear
import com.epfl.drawyourpath.challenge.Statistics.getTotalDistance
import com.epfl.drawyourpath.challenge.Statistics.getTotalTime
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.GlobalStatsFragment
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.GraphFromListFragment
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.TableFromListFragment
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.epfl.drawyourpath.utils.Utils

/**
 * This fragment is used to display some statistics of the user past activities.
 * @param averageSpeed average speed in total of the user (used for test, default value is null)
 * @param averageSpeedPerMonth average speed of the user per month (used for test, default value is null)
 * @param averageSpeedPerYear average speed of the user per year (used for test, default value is null)
 * @param averageDuration average duration in total activity (used for test, default value is null)
 * @param averageDurationPerMonth average duration per month (used for test, default value is null)
 * @param averageDurationPerYear average duration per year (used for test, default value is null)
 * @param averageDistance average distance in total (used for test, default value is null)
 * @param averageDistancePerMonth average distance per month (used for test, default value is null)
 * @param averageDistancePerYear average distance per year (used for test, default value is null)
 * @param totalDistanceGoal total number of distance goal reached(used for test, default value is null)
 * @param totalDistanceGoalPerYear number of distance goal reached per year(used for test, default value is null)
 * @param totalActivityTimeGoal number of activity time goal reached(used for test, default value is null)
 * @param totalActivityTimeGoalPerYear total number of activity time goal reached per year(used for test, default value is null)
 * @param totalPathNumberGoal total number of path number goal reached(used for test, default value is null)
 * @param totalPathNumberGoalPerYear number of path number goal reached per year(used for test, default value is null)
 */
class StatsFragment(
    private var averageSpeed: Double? = null,
    private var averageSpeedPerMonth: Map<Double, Double>? = null,
    private var averageSpeedPerYear: Map<Double, Double>? = null,
    private var averageDuration: Double? = null,
    private var averageDurationPerMonth: Map<Double, Double>? = null,
    private var averageDurationPerYear: Map<Double, Double>? = null,
    private var averageDistance: Double? = null,
    private var averageDistancePerMonth: Map<Double, Double>? = null,
    private var averageDistancePerYear: Map<Double, Double>? = null,
    private var totalDistanceGoal: Double? = null,
    private var totalDistanceGoalPerYear: Map<Double, Double>? = null,
    private var totalActivityTimeGoal: Double? = null,
    private var totalActivityTimeGoalPerYear: Map<Double, Double>? = null,
    private var totalPathNumberGoal: Double? = null,
    private var totalPathNumberGoalPerYear: Map<Double, Double>? = null,
) : Fragment(R.layout.fragment_stats) {
    private lateinit var titleText: TextView
    private lateinit var changeLeftButton: TextView
    private lateinit var changeRightButton: Button
    private lateinit var toggleDuration: ToggleButton
    private lateinit var descriptionLayout: FrameLayout
    private lateinit var currentStateView: StatsEnum
    private val userModelCached: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the elements of the view
        initViewElements(view)
        // at the initial state display the global statistics
        this.currentStateView = StatsEnum.GLOBAL_STATS
        show()
        // defined the transition of the right button
        rightTransitionButton()
        // defined the transition of the left button
        leftTransitionButton()
        // defined the transition of the toggle button
        togglePeriodButton()
    }

    /**
     * Helper functions to defined the transitions made by clicking on the toggle button to defined the period of the stats
     */
    private fun togglePeriodButton() {
        this.toggleDuration.setOnClickListener {
            val newIndex =
                Math.floorMod(this.currentStateView.index + this.currentStateView.toggleStep, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on right transition button.
     */
    private fun rightTransitionButton() {
        this.changeRightButton.setOnClickListener {
            val newIndex =
                Math.floorMod(this.currentStateView.index + this.currentStateView.rightStep, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on left transition button.
     */
    private fun leftTransitionButton() {
        this.changeLeftButton.setOnClickListener {
            val newIndex =
                Math.floorMod(this.currentStateView.index + this.currentStateView.leftStep, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper function to show the correct information's in the view in function of the current state of the RunInfoStetEnum
     */
    private fun show() {
        when (currentStateView) {
            StatsEnum.GLOBAL_STATS ->
                showGlobalStats(
                    averageSpeed ?: 0.0,
                    averageDuration ?: 0.0,
                    averageDistance ?: 0.0,
                    totalDistanceGoal ?: 0.0,
                    totalActivityTimeGoal ?: 0.0,
                    totalPathNumberGoal ?: 0.0,
                )
            StatsEnum.AVERAGE_SPEED_PER_YEAR ->
                showAverageSpeedPerYear(averageSpeedPerYear ?: emptyMap())
            StatsEnum.AVERAGE_SPEED_PER_MONTH ->
                showAverageSpeedPerMonth(averageSpeedPerMonth ?: emptyMap())
            StatsEnum.AVERAGE_DURATION_PER_YEAR ->
                showAverageDurationPerYear(averageDurationPerYear ?: emptyMap())
            StatsEnum.AVERAGE_DURATION_PER_MONTH ->
                showAverageDurationPerMonth(averageDurationPerMonth ?: emptyMap())
            StatsEnum.AVERAGE_DISTANCE_PER_YEAR ->
                showAverageDistancePerYear(averageDistancePerYear ?: emptyMap())
            StatsEnum.AVERAGE_DISTANCE_PER_MONTH ->
                showAverageDistancePerMonth(averageDistancePerMonth ?: emptyMap())
            StatsEnum.TOTAL_DISTANCE_GOAL_PER_YEAR ->
                showTotalDistanceGoalPerYear(totalDistanceGoalPerYear ?: emptyMap())
            StatsEnum.TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR ->
                showTotalActivityTimeGoalPerYear(totalActivityTimeGoalPerYear ?: emptyMap())
            StatsEnum.TOTAL_PATH_NUMBER_GOAL_PER_YEAR ->
                showTotalPathNumberGoalPerYear(totalPathNumberGoalPerYear ?: emptyMap())
        }
        userModelCached.getDailyGoals().observe(viewLifecycleOwner) {
            when (currentStateView) {
                StatsEnum.GLOBAL_STATS ->
                    showGlobalStats(
                    averageSpeed ?: getAverageSpeed(it),
                    averageDuration ?: getAverageDuration(it),
                    averageDistance ?: getAverageDistance(it),
                    totalDistanceGoal ?: getTotalDistance(it),
                    totalActivityTimeGoal ?: getTotalTime(it),
                    totalPathNumberGoal ?: getShapeDrawnCount(it).toDouble(),
                    )
                StatsEnum.AVERAGE_SPEED_PER_YEAR ->
                    showAverageSpeedPerYear(averageSpeedPerYear ?: getAverageSpeedPerYear(it))
                StatsEnum.AVERAGE_SPEED_PER_MONTH ->
                    showAverageSpeedPerMonth(averageSpeedPerMonth ?: getAverageSpeedPerMonth(it))
                StatsEnum.AVERAGE_DURATION_PER_YEAR ->
                    showAverageDurationPerYear(averageDurationPerYear ?: getAverageDurationPerYear(it))
                StatsEnum.AVERAGE_DURATION_PER_MONTH ->
                    showAverageDurationPerMonth(averageDurationPerMonth ?: getAverageDurationPerMonth(it))
                StatsEnum.AVERAGE_DISTANCE_PER_YEAR ->
                    showAverageDistancePerYear(averageDistancePerYear ?: getAverageDistancePerYear(it))
                StatsEnum.AVERAGE_DISTANCE_PER_MONTH ->
                    showAverageDistancePerMonth(averageDistancePerMonth ?: getAverageDistancePerMonth(it))
                StatsEnum.TOTAL_DISTANCE_GOAL_PER_YEAR ->
                    showTotalDistanceGoalPerYear(totalDistanceGoalPerYear ?: getDistancePerYear(it))
                StatsEnum.TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR ->
                    showTotalActivityTimeGoalPerYear(totalActivityTimeGoalPerYear ?: getTimePerYear(it))
                StatsEnum.TOTAL_PATH_NUMBER_GOAL_PER_YEAR ->
                    showTotalPathNumberGoalPerYear(totalPathNumberGoalPerYear ?: getShapeDrawnCountPerYear(it))
            }
        }
    }

    /**
     * Helper function to init the textview and the button of the fragment.
     * @param view where the elements are present
     */
    private fun initViewElements(view: View) {
        this.titleText = view.findViewById(R.id.statsTitleView)
        this.changeLeftButton = view.findViewById(R.id.changeLeftStats)
        this.changeRightButton = view.findViewById(R.id.changeRightStats)
        this.toggleDuration = view.findViewById(R.id.toggleStats)
        this.descriptionLayout = view.findViewById(R.id.contentDescriptionStats)
    }

    /**
     * Helper function to show a graph with the given data and the given title elements in the preview layout of the stats fragments
     * @param data that will be show in the graph
     * @param lineText title of the X axe
     * @param columnText title of the Y axe
     */
    private fun showGraphInPreview(data: Map<Double, Double>, lineText: String, columnText: String) {
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = data, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
    }

    /**
     * Helper function to show a table, with the given data in string format and the given title of the two columns, in the description layout of the stats fragments
     * @param data in a string format that will be show in the table
     * @param lineText title of the X axe
     * @param columnText title of the Y axe
     */
    private fun showTableInDescription(data: Map<String, String>, lineText: String, columnText: String) {
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = data, column1Name = columnText, column2Name = lineText),
        ).commit()
        descriptionLayout.visibility = VISIBLE
    }

    /**
     * Helper function to set the toggle to select a period between month and year to be selectable and visible.
     * @param currentState of the toggle in this view(can be Month or Year) in string format
     */
    private fun setToggleVisibleSelectable(currentState: String) {
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(currentState)
        this.toggleDuration.isEnabled = true
    }

    /**
     * Helper function to set the toggle to select a period to be invisible(and so not selectable)
     */
    private fun setToggleInvisible() {
        this.toggleDuration.visibility = INVISIBLE
        this.toggleDuration.isEnabled = false
    }

    /**
     * Helper function to set the toggle to select a period to be not selectable and block on the Year value.
     */
    private fun setToggleInSelectableOnYear() {
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = false
    }

    /**
     * Helper function to adapt the view to display the global stats of the user during his drawing session.
     */
    private fun showGlobalStats(
        currentAverageSpeed: Double,
        currentAverageDuration: Double,
        currentAverageDistance: Double,
        currentTotalDistanceGoal: Double,
        currentTotalActivityTimeGoal: Double,
        currentTotalPathNumberGoal: Double,
    ) {
        this.titleText.text = getString(R.string.global_stats)
        // not show the toggle button
        setToggleInvisible()
        // show the global stats
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GlobalStatsFragment(
                averageSpeed = currentAverageSpeed,
                averageDuration = currentAverageDuration,
                averageDistance = currentAverageDistance,
                totalDistanceGoal = currentTotalDistanceGoal,
                totalActivityTimeGoal = currentTotalActivityTimeGoal,
                totalPathNumberGoal = currentTotalPathNumberGoal,
            ),
        ).commit()
        // set the description layout an empty fragment
        descriptionLayout.visibility = INVISIBLE
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per year.
     */
    private fun showAverageSpeedPerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_speed)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_speed_in_m_s)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average speed in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average speed in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringSpeed(speed = value)
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per month.
     */
    private fun showAverageSpeedPerMonth(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_speed)
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_speed_in_m_s)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average speed in function of the month
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average speed in function of the month
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringSpeed(speed = value)
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average duration per year.
     */
    private fun showAverageDurationPerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_duration)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_duration_in_s)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average duration in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average duration in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per month.
     */
    private fun showAverageDurationPerMonth(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_duration)
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_duration_in_s)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average duration in function of the month
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average duration in function of the month
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average distance per year.
     */
    private fun showAverageDistancePerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_distance)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_distance_in_m)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average distance in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average distance in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average distance per month.
     */
    private fun showAverageDistancePerMonth(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.average_distance)
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_distance_in_m)
        // show the toggle button
        setToggleVisibleSelectable(currentState = columnText)
        // show a graph of the average distance in function of the month
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the average distance in function of the month
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the total distance goal per year.
     */
    private fun showTotalDistanceGoalPerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.distance_goal)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.distance_goal)
        // show the toggle button and block it to year
        setToggleInSelectableOnYear()
        // show a graph of the distance goal in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the distance goal in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view with a graph and a table to display the total activity time goal per year.
     */
    private fun showTotalActivityTimeGoalPerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.activity_time_goal)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.activity_time_goal)
        // show the toggle button and block it to year
        setToggleInSelectableOnYear()
        // show a graph of the activity time goal in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the activity time goal in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the total path number goal per year.
     */
    private fun showTotalPathNumberGoalPerYear(data: Map<Double, Double>) {
        this.titleText.text = getString(R.string.path_number_goal)
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.path_number_goal)
        // show the toggle button and block it to year
        setToggleInSelectableOnYear()
        // show a graph of the distance goal in function of the year
        showGraphInPreview(data = data, lineText = lineText, columnText = columnText)
        // show a table containing the distance goal in function of the year
        val mapString = hashMapOf<String, String>()
        data.forEach { (key, value) ->
            mapString[key.toString()] = value.toString()
        }
        showTableInDescription(data = mapString, lineText = lineText, columnText = columnText)
    }
}

/**
 * This enum is used to defined the different state of the stats fragment.
 * @param index of the step in the enum
 * @param rightStep step transition associate to the right button.
 * @param leftStep step transition associate to the left button(negative value).
 * @param toggleStep step associate to the toggle button(0 if it is not defined, in cae of the general stats and the goals stats)(can be positive or negative)
 */
private enum class StatsEnum(val index: Int, val rightStep: Int, val leftStep: Int, val toggleStep: Int) {
    GLOBAL_STATS(index = 0, rightStep = 1, leftStep = -1, toggleStep = 0),
    AVERAGE_SPEED_PER_YEAR(index = 1, rightStep = 2, leftStep = -1, toggleStep = 1),
    AVERAGE_SPEED_PER_MONTH(index = 2, rightStep = 2, leftStep = -2, toggleStep = -1),
    AVERAGE_DURATION_PER_YEAR(index = 3, rightStep = 2, leftStep = -2, toggleStep = 1),
    AVERAGE_DURATION_PER_MONTH(index = 4, rightStep = 2, leftStep = -2, toggleStep = -1),
    AVERAGE_DISTANCE_PER_YEAR(index = 5, rightStep = 2, leftStep = -2, toggleStep = 1),
    AVERAGE_DISTANCE_PER_MONTH(index = 6, rightStep = 1, leftStep = -2, toggleStep = -1),
    TOTAL_DISTANCE_GOAL_PER_YEAR(index = 7, rightStep = 1, leftStep = -2, toggleStep = 0),
    TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR(index = 8, rightStep = 1, leftStep = -1, toggleStep = 0),
    TOTAL_PATH_NUMBER_GOAL_PER_YEAR(index = 9, rightStep = 1, leftStep = -1, toggleStep = 0),
}
