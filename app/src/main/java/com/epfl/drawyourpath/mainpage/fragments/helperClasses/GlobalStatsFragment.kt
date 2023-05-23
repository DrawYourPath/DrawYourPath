package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.utils.Utils

/**
 * This fragment is used to display  a list of the global stats of the user
 * @param averageSpeed average speed in total of the user
 * @param averageDuration average duration in total activity
 * @param averageDistance average distance in total
 * @param totalDistanceGoal total number of distance goal reached
 * @param totalActivityTimeGoal number of activity time goal reached
 * @param totalPathNumberGoal total number of path number goal reached
 */
class GlobalStatsFragment(
    private val averageSpeed: Double,
    private val averageDuration: Double,
    private val averageDistance: Double,
    private val totalDistanceGoal: Double,
    private val totalActivityTimeGoal: Double,
    private val totalPathNumberGoal: Double,
) : Fragment(R.layout.fragment_global_stats) {
    private lateinit var textAvgSpeed: TextView
    private lateinit var textAvgDuration: TextView
    private lateinit var textAvgDistance: TextView
    private lateinit var textDistanceGoal: TextView
    private lateinit var textTimeGoal: TextView
    private lateinit var textPathGoal: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the elements of the view
        initViewElements(view)
        //set the text for each text view
        this.textAvgSpeed.text =
            "${getString(R.string.average_speed_in_m_s_title)} ${Utils.getStringSpeed(speed = averageSpeed)}"
        this.textAvgDuration.text =
            "${getString(R.string.average_duration_in_s_title)} ${Utils.getStringDuration(time = averageDuration.toLong())}"
        this.textAvgDistance.text =
            "${getString(R.string.average_distance_in_m_title)} ${Utils.getStringDistance(distance = averageDistance)}"
        this.textDistanceGoal.text =
            "${getString(R.string.number_of_distance_goal_reached)} $totalDistanceGoal"
        this.textTimeGoal.text =
            "${getString(R.string.number_of_time_goal_reached)} $totalActivityTimeGoal"
        this.textPathGoal.text =
            "${getString(R.string.number_of_path_goal_reached)} $totalPathNumberGoal"
    }

    /**
     * Helper function to init the textview and the button of the fragment.
     * @param view where the elements are present
     */
    private fun initViewElements(view: View) {
        this.textAvgSpeed = view.findViewById(R.id.average_speed_global_stats)
        this.textAvgDuration = view.findViewById(R.id.average_duration_global_stats)
        this.textAvgDistance = view.findViewById(R.id.average_distance_global_stats)
        this.textDistanceGoal = view.findViewById(R.id.distance_goal_global_stats)
        this.textTimeGoal = view.findViewById(R.id.time_goal_global_stats)
        this.textPathGoal = view.findViewById(R.id.path_goal_global_stats)
    }

}