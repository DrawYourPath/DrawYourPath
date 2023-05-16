package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R

/**
 * This class is used to show the different fragments to draw a path(countdown, draw a path, see user performance, end view to return back to the menu)
 */
class PathDrawingContainerFragment(private val countdownDuration: Long = 4) : Fragment(R.layout.activity_path_drawing_actvity) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.path_drawing_activity_content, PathDrawingCountDownFragment(countdownDuration = countdownDuration)).commit()

    }

}
