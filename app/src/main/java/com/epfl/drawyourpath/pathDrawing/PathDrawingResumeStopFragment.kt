package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run

class PathDrawingResumeStopFragment(private val run: Run) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_resume_stop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //update the main view if the resume button is clicked
        val resumeButton: Button = view.findViewById(R.id.resume_drawing_button)
        resumeButton.setOnClickListener {
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.contentFragmentPathDrawing, PathDrawingMainFragment(isDrawing = true))
            fragTransaction.commit()
        }

        //show the end draw path view by clicking on the End the draw button
        val stopButton: Button = view.findViewById(R.id.stop_drawing_button)
        stopButton.setOnClickListener {
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.contentFragmentPathDrawing, PathDrawingEndFragment(run))
            fragTransaction.commit()
        }
    }
}