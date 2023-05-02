package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R

class PathDrawingPauseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_pause, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // update the main view if the pause button is clicked
        val pauseButton: Button = view.findViewById(R.id.pause_drawing_button)
        pauseButton.setOnClickListener {
            val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.contentFragmentPathDrawing, PathDrawingMainFragment(isDrawing = false))
            fragTransaction.commit()
        }
    }
}
