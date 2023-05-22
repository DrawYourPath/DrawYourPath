package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.runStats.GraphFromListFragment
import com.epfl.drawyourpath.mainpage.fragments.runStats.TableFromListFragment
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
class StatsFragments(
    private val averageSpeed: Double? = null,
    private val averageSpeedPerMonth: Map<Double, Double>? = null,
    private val averageSpeedPerYear: Map<Double, Double>? = null,
    private val averageDuration: Double? = null,
    private val averageDurationPerMonth: Map<Double, Double>? = null,
    private val averageDurationPerYear: Map<Double, Double>? = null,
    private val averageDistance: Double? = null,
    private val averageDistancePerMonth: Map<Double, Double>? = null,
    private val averageDistancePerYear: Map<Double, Double>? = null,
    private val totalDistanceGoal: Double? = null,
    private val totalDistanceGoalPerYear: Map<Double, Double>? = null,
    private val totalActivityTimeGoal: Double? = null,
    private val totalActivityTimeGoalPerYear: Map<Double, Double>? = null,
    private val totalPathNumberGoal: Double? = null,
    private val totalPathNumberGoalPerYear: Map<Double, Double>? = null,
) : Fragment(R.layout.fragment_stats) {
    private lateinit var titleText: TextView
    private lateinit var changeLeftButton: TextView
    private lateinit var changeRightButton: Button
    private lateinit var toggleDuration: ToggleButton

    private lateinit var currentStateView: StatsEnum

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
        //defined the transition of the toggle button
        togglePeriodButton()
    }

    /**
     * Helper functions to defined the transitions made by clicking on the toggle button to defined the period of the stats
     */
    private fun togglePeriodButton() {
        this.toggleDuration.setOnClickListener {
            val step: Int = if (this.currentStateView.index % 2 == 0) {
                -1
            } else {
                1
            }
            val newIndex =
                Math.floorMod(this.currentStateView.index + step, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on right transition button.
     */
    private fun rightTransitionButton() {
        this.changeRightButton.setOnClickListener {
            val step = if (isViewPeriodToggle()) { 1 } else { 2 }
            val newIndex =
                Math.floorMod(this.currentStateView.index + step, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on left transition button.
     */
    private fun leftTransitionButton() {
        this.changeLeftButton.setOnClickListener {
            val step = if (isViewPeriodToggle()) { -1 } else { -2 }
            val newIndex =
                Math.floorMod(this.currentStateView.index + step, StatsEnum.values().size)
            this.currentStateView = StatsEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper function to know is we are in view without a toggle period with month/year
     * @return a boolean to indicate if it is the case
     */
    private fun isViewPeriodToggle(): Boolean{
         return (this.currentStateView == StatsEnum.GLOBAL_STATS || this.currentStateView == StatsEnum.TOTAL_DISTANCE_GOAL_PER_YEAR ||
                this.currentStateView == StatsEnum.TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR || this.currentStateView == StatsEnum.TOTAL_PATH_NUMBER_GOAL_PER_YEAR)
    }

    /**
     * Helper function to show the correct information's in the view in function of the current state of the RunInfoStetEnum
     */
    private fun show() {
        when (currentStateView) {
            StatsEnum.GLOBAL_STATS -> showGlobalStats()
            StatsEnum.AVERAGE_SPEED_PER_YEAR -> showAverageSpeedPerYear()
            StatsEnum.AVERAGE_SPEED_PER_MONTH -> showAverageSpeedPerMonth()
            StatsEnum.AVERAGE_DURATION_PER_YEAR -> showAverageDurationPerYear()
            StatsEnum.AVERAGE_DURATION_PER_MONTH -> showAverageDurationPerMonth()
            StatsEnum.AVERAGE_DISTANCE_PER_YEAR -> showAverageDistancePerYear()
            StatsEnum.AVERAGE_DISTANCE_PER_MONTH -> showAverageDistancePerMonth()
            StatsEnum.TOTAL_DISTANCE_GOAL_PER_YEAR -> showTotalDistanceGoalPerYear()
            StatsEnum.TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR -> showTotalActivityTimeGoalPerYear()
            StatsEnum.TOTAL_PATH_NUMBER_GOAL_PER_YEAR -> showTotalPathNumberGoalPerYear()
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
    }


    /**
     * Helper function to adapt the view to display the global stats of the user during his drawing session.
     */
    private fun showGlobalStats() {
        this.titleText.text = getString(R.string.global_stats)
        //not show the toggle button
        this.toggleDuration.visibility = INVISIBLE
        this.toggleDuration.isEnabled = false
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per year.
     */
    private fun showAverageSpeedPerYear() {
        this.titleText.text = getString(R.string.average_speed)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = true
        // show a graph of the average speed in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_speed_in_m_s)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageSpeedPerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average speed in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageSpeedPerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringSpeed(speed = value)
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per month.
     */
    private fun showAverageSpeedPerMonth() {
        this.titleText.text = getString(R.string.average_speed)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.month))
        this.toggleDuration.isEnabled = true
        // show a graph of the average speed in function of the month
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_speed_in_m_s)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageSpeedPerMonth?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average speed in function of the month
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageDistancePerMonth?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringSpeed(speed = value)
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average duration per year.
     */
    private fun showAverageDurationPerYear() {
        this.titleText.text = getString(R.string.average_duration)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = true
        // show a graph of the average duration in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_duration_in_s)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageDurationPerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average duration in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageDurationPerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per month.
     */
    private fun showAverageDurationPerMonth() {
        this.titleText.text = getString(R.string.average_duration)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.month))
        this.toggleDuration.isEnabled = true
        // show a graph of the average duration in function of the month
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_duration_in_s)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageDurationPerMonth?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average duration in function of the month
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageDurationPerMonth?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average distance per year.
     */
    private fun showAverageDistancePerYear() {
        this.titleText.text = getString(R.string.average_distance)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = true
        // show a graph of the average distance in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.average_distance_in_m)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageDistancePerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average distance in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageDistancePerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average distance per month.
     */
    private fun showAverageDistancePerMonth() {
        this.titleText.text = getString(R.string.average_distance)
        //show the toggle button
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.month))
        this.toggleDuration.isEnabled = true
        // show a graph of the average distance in function of the month
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.month)
        val lineText = getString(R.string.average_distance_in_m)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = averageDistancePerMonth?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the average distance in function of the month
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (averageDistancePerMonth?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the total distance goal per year.
     */
    private fun showTotalDistanceGoalPerYear() {
        this.titleText.text = getString(R.string.distance_goal)
        //show the toggle button and block it to year
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = false
        // show a graph of the distance goal in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.distance_goal_in_m)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = totalDistanceGoalPerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the distance goal in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (totalDistanceGoalPerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDistance(distance = value)
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the total activity time goal per year.
     */
    private fun showTotalActivityTimeGoalPerYear() {
        this.titleText.text = getString(R.string.activity_time_goal)
        //show the toggle button and block it to year
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = false
        // show a graph of the activity time goal in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.activity_time_goal_in_s)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = totalActivityTimeGoalPerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the activity time goal in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (totalActivityTimeGoalPerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = Utils.getStringDuration(time = value.toLong())
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the total path number goal per year.
     */
    private fun showTotalPathNumberGoalPerYear() {
        this.titleText.text = getString(R.string.path_number_goal)
        //show the toggle button and block it to year
        this.toggleDuration.visibility = VISIBLE
        this.toggleDuration.setText(getString(R.string.year))
        this.toggleDuration.isEnabled = false
        // show a graph of the distance goal in function of the year
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val columnText = getString(R.string.year)
        val lineText = getString(R.string.path_number_goal)
        fragTransaction.replace(
            R.id.contentPreviewStats,
            GraphFromListFragment(map = totalPathNumberGoalPerYear?: emptyMap(), titleAxe1 = columnText, titleAxe2 = lineText)
        ).commit()
        // show a table containing the distance goal in function of the year
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        (totalPathNumberGoalPerYear?: emptyMap()).forEach{ (key, value) ->
            mapString[key.toString()] = value.toString()
        }
        fragTransaction2.replace(
            R.id.contentDescriptionStats,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)
        ).commit()
    }
}

/**
 * This enum is used to defined the different state of the stats fragment.
 */
private enum class StatsEnum(val index: Int) {
    GLOBAL_STATS(0), AVERAGE_SPEED_PER_YEAR(1), AVERAGE_SPEED_PER_MONTH(2), AVERAGE_DURATION_PER_YEAR(
        3
    ),
    AVERAGE_DURATION_PER_MONTH(4), AVERAGE_DISTANCE_PER_YEAR(5), AVERAGE_DISTANCE_PER_MONTH(6), TOTAL_DISTANCE_GOAL_PER_YEAR(
        7
    ),
    TOTAL_ACTIVITY_TIME_GOAL_PER_YEAR(8), TOTAL_PATH_NUMBER_GOAL_PER_YEAR(9),
}