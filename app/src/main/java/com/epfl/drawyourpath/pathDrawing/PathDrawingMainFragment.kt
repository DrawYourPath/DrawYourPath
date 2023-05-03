package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run

/**
 * The main fragment where the map and current sport will be displayed when the user will draw his path
 * @param run that the user had made(contains past performance and path)
 * @param isDrawing to know if the user is currently drawing a path or if the path is in pause state
 */
class PathDrawingMainFragment(private val run: Run, private val isDrawing: Boolean) : Fragment(R.layout.fragment_path_drawing_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isDrawing) {
            showMap()
            showCurrentSportData()
            showPauseButton()
        } else {
            showPathPreview()
            showResumeStopButton()
            showDetailSportData()
        }
    }

    /**
     * Function used to display the fragment that show the current sport data of the user
     */
    private fun showCurrentSportData() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_performance, PathDrawingCurrentPerformanceFragment(run = run))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show the detail performance data to the user
     */
    private fun showDetailSportData() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_performance, PathDrawingDetailPerformanceFragment(run = run))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show the map to the user to the user
     */
    private fun showMap() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_map, MapFragment(focusedOnPosition = true, path = run.getPath()))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a preview of the path currently drawn by the user during his run
     */
    private fun showPathPreview() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_map, MapFragment(focusedOnPosition = false, path = run.getPath()))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a button to pause the path drawing
     */
    private fun showPauseButton() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_buttons, PathDrawingPauseFragment(run = run))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a button to resume and a button to end the path drawing
     */
    private fun showResumeStopButton() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_main_buttons, PathDrawingResumeStopFragment(run = run))
        fragTransaction.commit()
    }
}
