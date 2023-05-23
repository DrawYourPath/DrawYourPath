package com.epfl.drawyourpath.path

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.GraphFromListFragment
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ShapePathDescriptionFragment
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.TableFromListFragment
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.pathDrawing.PathDrawingDetailPerformanceFragment
import com.epfl.drawyourpath.utils.Utils

/**
 * This enum is used to defined the different state of the info/stats fragment to display different information in function of this state
 */
private enum class RunInfoStatesEnum(val index: Int) {
    PATH_DRAWN(0), GLOBAL_STATS(1), AVERAGE_SPEED_KM(2), DURATION_KM(3), DISTANCE_SEGMENT(4), DURATION_SEGMENT(
        5,
    ),
    AVERAGE_SPEED_SEGMENT(6),
}

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
        this.currentStateView = RunInfoStatesEnum.PATH_DRAWN
        show()
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
            val newIndex =
                Math.floorMod(this.currentStateView.index + 1, RunInfoStatesEnum.values().size)
            this.currentStateView = RunInfoStatesEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper functions to defined the transitions made by clicking on left transition button.
     */
    private fun leftTransitionButton() {
        this.changeLeftButton.setOnClickListener {
            val newIndex =
                Math.floorMod(this.currentStateView.index - 1, RunInfoStatesEnum.values().size)
            this.currentStateView = RunInfoStatesEnum.values()[newIndex]
            show()
        }
    }

    /**
     * Helper function to show the correct information's in the view in function of the current state of the RunInfoStetEnum
     */
    private fun show() {
        when (currentStateView) {
            RunInfoStatesEnum.PATH_DRAWN -> showPathDrawn()
            RunInfoStatesEnum.GLOBAL_STATS -> showGlobalStats()
            RunInfoStatesEnum.AVERAGE_SPEED_KM -> showAverageSpeedKm()
            RunInfoStatesEnum.DURATION_KM -> showDurationKm()
            RunInfoStatesEnum.DISTANCE_SEGMENT -> showDistanceSegment()
            RunInfoStatesEnum.DURATION_SEGMENT -> showDurationSegment()
            RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT -> showAverageSpeedSegment()
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
        this.titleText.text = getString(R.string.path_drawn)
        // lunch the fragment to display the map with the path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            MapFragment(focusedOnPosition = false, path = run.getPath()),
        ).commit()
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            ShapePathDescriptionFragment(
                formName = run.predictedShape,
                score = run.similarityScore,
            ),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display the global stats of the user during his drawing session.
     */
    private fun showGlobalStats() {
        this.titleText.text = getString(R.string.global_stats)
        // lunch the fragment to display the map with the path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            MapFragment(focusedOnPosition = false, path = run.getPath()),
        ).commit()
        // lunch the fragment to display all the dta of the run
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            PathDrawingDetailPerformanceFragment(run = run),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per km.
     */
    private fun showAverageSpeedKm() {
        this.titleText.text = getString(R.string.average_speed_per_km)
        // show a graph of the speed in function of the kilometers
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapSpeed = hashMapOf<Double, Double>()
        val listSpeed = run.getKilometersAvgSpeed()
        listSpeed.forEachIndexed { index, elem ->
            mapSpeed.put((index + 1).toDouble(), elem)
        }
        val columnText = getString(R.string.distance_in_km)
        val lineText = getString(R.string.average_speed_in_m_s)
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            GraphFromListFragment(map = mapSpeed, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
        // show a table containing the speed in function of the km
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listSpeed.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringSpeed(speed = elem))
        }
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the durations per km.
     */
    private fun showDurationKm() {
        this.titleText.text = getString(R.string.duration_per_km)
        // show a graph of the duration in function of the kilometers
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapDuration = hashMapOf<Double, Double>()
        val listDuration = run.getKilometersDuration()
        listDuration.forEachIndexed { index, elem ->
            mapDuration.put((index + 1).toDouble(), elem.toDouble())
        }
        val columnText = getString(R.string.distance_in_km)
        val lineText = getString(R.string.duration_in_s)
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            GraphFromListFragment(map = mapDuration, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
        // show a table containing the duration in function of the km
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDuration.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDuration(elem))
        }
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the distance per segment.
     */
    private fun showDistanceSegment() {
        this.titleText.text = getString(R.string.distance_per_segment)
        // show a graph of the distance in function of the section
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapDistance = hashMapOf<Double, Double>()
        val listDistance = run.getSectionsDistance()
        listDistance.forEachIndexed { index, elem ->
            mapDistance.put((index + 1).toDouble(), elem)
        }
        val columnText = getString(R.string.segment)
        val lineText = getString(R.string.distance_in_m)
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            GraphFromListFragment(map = mapDistance, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
        // show a table containing the distance in function of the section
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDistance.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDistance(elem))
        }
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the duration per segment.
     */
    private fun showDurationSegment() {
        this.titleText.text = getString(R.string.duration_per_segment)
        // show a graph of the duration in function of the section
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapDuration = hashMapOf<Double, Double>()
        val listDuration = run.getSectionsDuration()
        listDuration.forEachIndexed { index, elem ->
            mapDuration.put((index + 1).toDouble(), elem.toDouble())
        }
        val columnText = getString(R.string.segment)
        val lineText = getString(R.string.duration_in_s)
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            GraphFromListFragment(map = mapDuration, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
        // show a table containing the duration in function of the section
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDuration.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDuration(elem))
        }
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText),
        ).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per segment.
     */
    private fun showAverageSpeedSegment() {
        this.titleText.text = getString(R.string.average_speed_per_segment)
        // show a graph of the average speed in function of the section
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapSpeed = hashMapOf<Double, Double>()
        val listSpeed = run.getSectionsAvgSpeed()
        listSpeed.forEachIndexed { index, elem ->
            mapSpeed.put((index + 1).toDouble(), elem)
        }
        val columnText = getString(R.string.segment)
        val lineText = getString(R.string.average_speed_in_m_s)
        fragTransaction.replace(
            R.id.contentPreviewRunInfo,
            GraphFromListFragment(map = mapSpeed, titleAxe1 = columnText, titleAxe2 = lineText),
        ).commit()
        // show a table containing the average speed in function of the sections
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listSpeed.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringSpeed(elem))
        }
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText),
        ).commit()
    }
}
