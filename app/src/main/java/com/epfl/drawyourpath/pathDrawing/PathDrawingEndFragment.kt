package com.epfl.drawyourpath.pathDrawing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

/**
 * This fragment will be displayed at the end of a creation of a path (that will be displayed a preview of the path on the map,
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

        // return back to the menu add and save the path back clicking and the back to menu button
        backMenuButton = view.findViewById(R.id.path_drawing_end_back_menu_button)
        backMenuButton.setOnClickListener {
            userCached.addNewRun(pathDrawingModel.getRun()).thenApplyAsync {
                returnBackToMenu()
            }
        }
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
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_USER_ID, userCached.getUserId())
        this.startActivity(intent)
    }
}
