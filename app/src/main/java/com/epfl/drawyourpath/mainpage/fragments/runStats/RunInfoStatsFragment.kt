package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run

/**
 * This fragment is used to display some information and statistics relative to a run.
 *
 */
class RunInfoStatsFragment(private val run: Run) : Fragment(R.layout.fragment_run_info_stats) {
    private lateinit var titleText: TextView
    private lateinit var changeLeftButton: TextView
    private lateinit var changeRightButton: Button

    private lateinit var currentStateView: RunInfoStatesEnum

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the elements of the view
        initViewElements(view)
        // at the initial state display the path drawn information
        showPathDrawn()
        // defined the transition of the right button
        rightTransitionButton()
        // defined the transition of the left button
        leftTransitionButton()
    }

    /**
     * Helper functions to defined the transitions made by clicking on right transition button.
     */
    private fun rightTransitionButton() {
        this.changeRightButton.setOnClickListener {
            when (currentStateView) {
                RunInfoStatesEnum.PATH_DRAWN -> showGlobalStats()
                RunInfoStatesEnum.GLOBAL_STATS -> showAverageSpeedKm()
                RunInfoStatesEnum.AVERAGE_SPEED_KM -> showDurationKm()
                RunInfoStatesEnum.DURATION_KM -> showDistanceSegment()
                RunInfoStatesEnum.DISTANCE_SEGMENT -> showDurationSegment()
                RunInfoStatesEnum.DURATION_SEGMENT -> showAverageSpeedSegment()
                RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT -> showPathDrawn()
            }
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on left transition button.
     */
    private fun leftTransitionButton() {
        this.changeLeftButton.setOnClickListener {
            when (currentStateView) {
                RunInfoStatesEnum.PATH_DRAWN -> showAverageSpeedSegment()
                RunInfoStatesEnum.GLOBAL_STATS -> showDurationSegment()
                RunInfoStatesEnum.AVERAGE_SPEED_KM -> showDistanceSegment()
                RunInfoStatesEnum.DURATION_KM -> showDurationKm()
                RunInfoStatesEnum.DISTANCE_SEGMENT -> showAverageSpeedKm()
                RunInfoStatesEnum.DURATION_SEGMENT -> showGlobalStats()
                RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT -> showPathDrawn()
            }
        }
    }

    /**
     * Helper function to init the textview and the button of the fragment.
     * @param view where the elements are present
     */
    private fun initViewElements(view: View) {
        this.titleText = view.findViewById(R.id.titleRunInfo)
        this.changeLeftButton = view.findViewById(R.id.changeLeftRunInfo)
        this.changeRightButton = view.findViewById(R.id.changeRightRunInfo)
    }

    /**
     * Helper function to adapt the view to display the path drawn on the map and show the score gives by the ML model below
     */
    private fun showPathDrawn() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.PATH_DRAWN
        this.titleText.text = getString(R.string.path_drawn)
        // lunch the fragment to display the map with the path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.contentPreviewRunInfo, MapFragment(focusedOnPosition = false, path = run.getPath())).commit()
        // lunch the fragment to display the core and the form recognized
        val form: String = "Square"
        val score: Int = 60
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, FormPathDescriptionFragment(formName = form, score = score)).commit()
    }

    /**
     * Helper function to adapt the view to display the global stats of the user during his drawing session.
     */
    private fun showGlobalStats() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.GLOBAL_STATS
        this.titleText.text = getString(R.string.global_stats)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per km.
     */
    private fun showAverageSpeedKm() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.AVERAGE_SPEED_KM
        this.titleText.text = getString(R.string.average_speed_per_km)
        // show a graph of the speed in function of the kilometers
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapDouble = mapOf<Double, Double>(
            1.0 to 20.0,
            2.0 to 30.0,
        )
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapDouble, titleAxe1 = "Kilomters", titleAxe2 = "Average Speed (m/s)")).commit()
        // show a table containing the speed in function of the km
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapEx = mapOf<String, String>(
            "1" to "20m/s",
            "2" to "30m/s",
        )
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapEx, column1Name = "Kilometers", column2Name = "Average Speed (m/s)")).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the durations per km.
     */
    private fun showDurationKm() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DURATION_KM
        this.titleText.text = getString(R.string.duration_per_km)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the distance per segment.
     */
    private fun showDistanceSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DISTANCE_SEGMENT
        this.titleText.text = getString(R.string.distance_per_segment)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the duration per segment.
     */
    private fun showDurationSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DURATION_SEGMENT
        this.titleText.text = getString(R.string.duration_per_segment)
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per segment.
     */
    private fun showAverageSpeedSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT
        this.titleText.text = getString(R.string.average_speed_per_segment)
    }
}
private enum class RunInfoStatesEnum {
    PATH_DRAWN, GLOBAL_STATS, AVERAGE_SPEED_KM, DURATION_KM, DISTANCE_SEGMENT, DURATION_SEGMENT, AVERAGE_SPEED_SEGMENT
}
