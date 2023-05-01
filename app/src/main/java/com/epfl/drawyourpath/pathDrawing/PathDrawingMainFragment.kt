package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng

/**
 * The main fragment where the map and current sport will be displayed when the user will draw his path
 */
class PathDrawingMainFragment(private val run: Run = getRunData(), private val isDrawing: Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isDrawing) {
            showMap(view)
            showPauseButton(view)
        } else {
            showPathPreview(view, run)
            showResumeStopButton(view)
        }
        showSportData(view, run)
    }

    /**
     * Function used to display the fragment that show the sport data to the user
     * @param view used to display the sport data
     * @param run that contains the sport data
     */
    private fun showSportData(view: View, run: Run) {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_sport_data, PathDrawingSportDataFragment(run))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show the map to the user to the user
     * @param view used to display the map
     */
    private fun showMap(view: View) {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_map_or_path, MapFragment(showCurrentPosition = true, path = run.getPath()))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a preview of the path currently drawn by the user during his run
     * @param view used to display the path drawn by the user
     * @param run made by the user
     */
    private fun showPathPreview(view: View, run: Run) {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_map_or_path, MapFragment(showCurrentPosition = false, path = run.getPath()))
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a button to pause the path drawing
     * @param view used to display the button
     */
    private fun showPauseButton(view: View) {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_pause_or_playStop, PathDrawingPauseFragment())
        fragTransaction.commit()
    }

    /**
     * Function used to display the fragment that show a button to resume and a button to end the path drawing
     * @param view used to display the buttons
     */
    private fun showResumeStopButton(view: View) {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_pause_or_playStop, PathDrawingResumeStopFragment(run))
        fragTransaction.commit()
    }
}

/**
 * This function is used to generate a run
 */
private fun getRunData(): Run {
    val runs = mutableListOf<Run>()

    val point1 = LatLng(0.0, 0.0)
    val point2 = LatLng(0.001, 0.001)
    val point3 = LatLng(0.001, 0.001)
    val point4 = LatLng(0.0, 0.001)
    val points = listOf(point1, point2, point3, point4)
    val path = Path(points)
    val startTime = System.currentTimeMillis()
    val endTime = startTime + 4530 // 1h15min30s
    val run1 = Run(path, startTime, endTime)
    return run1
}
