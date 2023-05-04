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
 * This fragment is used to display a button to resume the drawing of a path and a button to stop the drawing and save the run.
 * @param run that will be displayed when we will be resume or will saved when we stopped.
 */
class PathDrawingResumeStopFragment(private val run: Run? = null) : Fragment(R.layout.fragment_path_drawing_resume_stop) {

    private val pathDrawingModel: PathDrawingModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // update the main view if the resume button is clicked
        val resumeButton: Button = view.findViewById(R.id.path_drawing_resume_button)
        resumeButton.setOnClickListener {
            if (run == null) {
                pathDrawingModel.pauseResumeRun()
            }
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.path_drawing_activity_content, PathDrawingMainFragment(isDrawing = true, run = run))
            fragTransaction.commit()
        }

        // show the end draw path view by clicking on the End the draw button
        val stopButton: Button = view.findViewById(R.id.path_drawing_stop_button)
        stopButton.setOnClickListener {
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.path_drawing_activity_content, PathDrawingEndFragment(run ?: pathDrawingModel.getRun()))
            fragTransaction.commit()
        }
    }
}
