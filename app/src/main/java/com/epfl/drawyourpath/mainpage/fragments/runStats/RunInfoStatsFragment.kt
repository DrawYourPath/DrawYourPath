package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run

/**
 * This fragment is used to display some information and statistics relative to a run.
 *
 */
class RunInfoStatsFragment(private val run: Run) : Fragment(R.layout.fragment_run_info_stats) {
    private lateinit var titleText: TextView
    private lateinit var changeLeftButton: TextView
    private lateinit var changeRightButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init the elements of the view
        initViewElements(view)

    }

    /**
     * Helper function to init the textview and the button of the fragment.
     * @param view where the elements are present
     */
    private fun initViewElements(view: View){
        this.titleText = view.findViewById(R.id.titleRunInfo)
        this.changeLeftButton = view.findViewById(R.id.changeLeftRunInfo)
        this.changeRightButton = view.findViewById(R.id.changeRightRunInfo)
    }

    /**
     * Helper function to adapt the view to display the path drawn on the map and show the score gives by the ML model below
     */
    private fun showPathDrawn(){
        this.titleText.text = getString(R.string.path_drawn)

    }
}
private enum class RunInfoStatesEnum{
    PATH_DRAWN, GLOBAL_STATS, AVERAGE_SPEED_KM, DURATION_KM, DISTANCE_SEGMENT, DURATION_SEGMENT, AVERAGE_SPEED_SEGMENT
}