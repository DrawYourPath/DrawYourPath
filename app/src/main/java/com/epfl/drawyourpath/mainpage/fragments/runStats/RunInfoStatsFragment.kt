package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.machineLearning.DigitalInk
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.pathDrawing.PathDrawingDetailPerformanceFragment
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.digitalink.Ink

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
                RunInfoStatesEnum.GLOBAL_STATS -> showPathDrawn()
                RunInfoStatesEnum.AVERAGE_SPEED_KM -> showGlobalStats()
                RunInfoStatesEnum.DURATION_KM -> showAverageSpeedKm()
                RunInfoStatesEnum.DISTANCE_SEGMENT -> showDurationKm()
                RunInfoStatesEnum.DURATION_SEGMENT -> showDistanceSegment()
                RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT -> showDurationSegment()
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
        val fragTransaction2: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction2.replace(
            R.id.contentDescriptionRunInfo,
            ShapePathDescriptionFragment(formName = "displayed soon, please wait...", score = 0),
        ).commit()
        // TODO:will be change later when the form and score will be store
        // lunch the fragment to display the core and the form recognized
        DigitalInk.downloadModelML().thenAccept { it ->
            val ink = Ink.builder()
            for (section in run.getPath().getPoints()) {
                val listCoord = mutableListOf<LatLng>()
                for (point in section) {
                    listCoord.add(point)
                }
                val stroke = Utils.coordinatesToStroke(listCoord)
                ink.addStroke(stroke)
            }

            DigitalInk.recognizeDrawingML(ink.build(), it).thenAccept { elem ->
                if (currentStateView == RunInfoStatesEnum.PATH_DRAWN) {
                    val fragTransaction2: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    fragTransaction2.replace(
                        R.id.contentDescriptionRunInfo,
                        ShapePathDescriptionFragment(formName = elem.candidates[0].text, score = elem.candidates[0].score!!.toInt()),
                    ).commit()
                }
            }
        }
    }

    /**
     * Helper function to adapt the view to display the global stats of the user during his drawing session.
     */
    private fun showGlobalStats() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.GLOBAL_STATS
        this.titleText.text = getString(R.string.global_stats)
        // lunch the fragment to display the map with the path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.contentPreviewRunInfo, MapFragment(focusedOnPosition = false, path = run.getPath())).commit()
        // lunch the fragment to display all the dta of the run
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, PathDrawingDetailPerformanceFragment(run = run)).commit()
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
        val mapSpeed = hashMapOf<Double, Double>()
        val listSpeed = run.getKilometersAvgSpeed()
        listSpeed.forEachIndexed { index, elem ->
            mapSpeed.put((index + 1).toDouble(), elem)
        }
        val columnText = getString(R.string.distance_in_km)
        val lineText = getString(R.string.average_speed_in_m_s)
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapSpeed, titleAxe1 = columnText, titleAxe2 = lineText)).commit()
        // show a table containing the speed in function of the km
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listSpeed.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringSpeed(speed = elem))
        }
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the durations per km.
     */
    private fun showDurationKm() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DURATION_KM
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
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapDuration, titleAxe1 = columnText, titleAxe2 = lineText)).commit()
        // show a table containing the duration in function of the km
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDuration.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDuration(elem))
        }
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the distance per segment.
     */
    private fun showDistanceSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DISTANCE_SEGMENT
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
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapDistance, titleAxe1 = columnText, titleAxe2 = lineText)).commit()
        // show a table containing the distance in function of the section
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDistance.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDistance(elem))
        }
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the duration per segment.
     */
    private fun showDurationSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.DURATION_SEGMENT
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
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapDuration, titleAxe1 = columnText, titleAxe2 = lineText)).commit()
        // show a table containing the duration in function of the section
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listDuration.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringDuration(elem))
        }
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)).commit()
    }

    /**
     * Helper function to adapt the view to display a graph and a table to display the average speed per segment.
     */
    private fun showAverageSpeedSegment() {
        // update the state of the view
        this.currentStateView = RunInfoStatesEnum.AVERAGE_SPEED_SEGMENT
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
        fragTransaction.replace(R.id.contentPreviewRunInfo, GraphFromListFragment(map = mapSpeed, titleAxe1 = columnText, titleAxe2 = lineText)).commit()
        // show a table containing the average speed in function of the section
        val fragTransaction2: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val mapString = hashMapOf<String, String>()
        listSpeed.forEachIndexed { index, elem ->
            mapString.put((index + 1).toString(), Utils.getStringSpeed(elem))
        }
        fragTransaction2.replace(R.id.contentDescriptionRunInfo, TableFromListFragment(map = mapString, column1Name = columnText, column2Name = lineText)).commit()
    }
}

/**
 * This enum is used to defined the different state of the info/stats fragment to display differents information in function of this state
 */
private enum class RunInfoStatesEnum {
    PATH_DRAWN, GLOBAL_STATS, AVERAGE_SPEED_KM, DURATION_KM, DISTANCE_SEGMENT, DURATION_SEGMENT, AVERAGE_SPEED_SEGMENT
}
