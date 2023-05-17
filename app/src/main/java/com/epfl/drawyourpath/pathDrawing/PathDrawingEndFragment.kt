package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.MainFragment
import com.epfl.drawyourpath.mainpage.fragments.runStats.ShapePathDescriptionFragment
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

/**
 * This fragment will be displayed at the end of the path's creation (that will be displayed a preview of the path on the map,
 * the final performance data of the user and a button to return back to the main menu of the app).
 * @param run that contains the performance dta and the path made by the user
 */
class PathDrawingEndFragment(private val run: Run? = null) : Fragment(R.layout.fragment_path_drawing_end) {
    private val userCached: UserModelCached by activityViewModels()
    private val pathDrawingModel: PathDrawingModel by activityViewModels()

    private lateinit var backMenuButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // display the detail final performance of the user
        setupPerformancePreview()

        // display the preview of the path
        setupPathPreview()

        // display the form recognized by the ML model
        setupPathDescription()

        // return back to the menu add and save the path back clicking and the back to menu button
        backMenuButton = view.findViewById(R.id.path_drawing_end_back_menu_button)
        backMenuButton.setOnClickListener {
            userCached.addNewRun(pathDrawingModel.getRun()).thenApplyAsync {
                pathDrawingModel.clearRun()
                returnBackToMenu()
            }
        }
    }

    /**
     * Helper function to setup the path description preview(form +score) in the correct place of the fragment
     */
    private fun setupPathDescription() {
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.path_drawing_end_form_description,
            ShapePathDescriptionFragment(formName = "displayed soon, please wait...", score = 0),
        ).commit()
        // TODO:will be change later when the form and score will be store
        // lunch the fragment to display the score and the form recognized
    }

    /**
     * Helper function to setup the path preview on the map in correct place of the fragment
     */
    private fun setupPathPreview() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_end_map, MapFragment(focusedOnPosition = false, path = run?.getPath()))
        fragTransaction.commit()
    }

    /**
     * Helper function to setup the preview of the performance data of the user in correct place of the fragment
     */
    private fun setupPerformancePreview() {
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_end_performance, PathDrawingDetailPerformanceFragment(run = run))
        fragTransaction.commit()
    }

    /**
     * Helper function to return back to the main menu of the app
     */
    private fun returnBackToMenu() {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container_view, MainFragment::class.java, null)
        }
    }
}
