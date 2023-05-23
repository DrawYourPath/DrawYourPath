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
import com.epfl.drawyourpath.utils.Utils
import com.google.mlkit.vision.digitalink.RecognitionCandidate
import java.util.concurrent.atomic.AtomicBoolean

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

        // get the ml result
        // TODO change to new implementation
//        val result = Utils.getRunRecognition(pathDrawingModel.getRun()).thenApplyAsync {
//            it.candidates[0]
//        }
        val result = Utils.getBestRunRecognitionCandidate(pathDrawingModel.getRun())

        val runToAdd = pathDrawingModel.getRun()

        val cancel = AtomicBoolean(false)

        // display the detail final performance of the user
        setupPerformancePreview()

        // display the preview of the path
        setupPathPreview()

        // display the shape recognized by the ML model (or loading message before the model is downloaded)
        setupPathDescription()
        result.thenApplyAsync {
            if (!cancel.get()) {
                setupPathDescription(it)
            }
        }

        // return back to the menu add and save the path back clicking and the back to menu button
        backMenuButton = view.findViewById(R.id.path_drawing_end_back_menu_button)
        backMenuButton.setOnClickListener {
            cancel.set(true)
            result.thenApplyAsync {
                Run(runToAdd.getPath(), runToAdd.getStartTime(), runToAdd.getDuration(), runToAdd.getEndTime(), it.text, it.score?.toDouble() ?: 0.0)
            }.thenComposeAsync {
                userCached.addNewRun(it)
            }
            pathDrawingModel.clearRun()
            returnBackToMenu()
        }
    }

    /**
     * Helper function to setup the path description preview(form +score) in the correct place of the fragment
     */
    private fun setupPathDescription(result: RecognitionCandidate? = null) {
        val fragTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(
            R.id.path_drawing_end_form_description,
            result?.let { ShapePathDescriptionFragment(formName = it.text, score = it.score?.toDouble() ?: 0.0) } ?: ShapePathDescriptionFragment(),
        ).commit()
        // launch the fragment to display the score and the form recognized
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
