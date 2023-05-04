package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run

/**
 * This fragment is used to display a button to pause the drawing of a path.
 * @param run that will be displayed when we will be pause.
 */
class PathDrawingPauseFragment(private val run: Run? = null) : Fragment(R.layout.fragment_path_drawing_pause) {

    private val pathDrawingModel: PathDrawingModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // update the main view if the pause button is clicked
        val pauseButton: Button = view.findViewById(R.id.path_drawing_pause_button)
        pauseButton.setOnClickListener {
            if (run == null) {
                pathDrawingModel.pauseResumeRun()
            }
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.path_drawing_activity_content, PathDrawingMainFragment(isDrawing = false, run = run ?: pathDrawingModel.getRun()))
            fragTransaction.commit()
        }
    }
}
