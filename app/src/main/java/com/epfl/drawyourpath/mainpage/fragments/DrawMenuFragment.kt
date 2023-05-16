package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.pathDrawing.PathDrawingContainerFragment

class DrawMenuFragment : Fragment(R.layout.fragment_draw_menu) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = MapFragment(focusedOnPosition = false, path = null)
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.mapMenuFragmentContent, mapFragment)
        fragTransaction.commit()

        // display the activity to draw a path when we click on start drawing button
        val startButton: Button = view.findViewById(R.id.button_start_drawing)
        startButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.main_fragment_container_view, PathDrawingContainerFragment::class.java, null)
            }
        }
    }

}
