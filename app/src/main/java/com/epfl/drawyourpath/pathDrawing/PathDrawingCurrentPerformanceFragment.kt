package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils.getStringDistance
import com.epfl.drawyourpath.utils.Utils.getStringDuration
import com.epfl.drawyourpath.utils.Utils.getStringSpeed

/**
 * Fragment used to displayed the performance of a user during a run(time, distance, average speed).
 * @param run that contains the performance data
 */
class PathDrawingCurrentPerformanceFragment(private val run: Run? = null) : Fragment(R.layout.fragment_path_drawing_current_performance) {
    private lateinit var textSpeed: TextView
    private lateinit var textDistance: TextView
    private lateinit var textTime: TextView

    private val pathDrawingModel: PathDrawingModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initiate the different text view
        textDistance = view.findViewById(R.id.display_distance_current_performance)
        textTime = view.findViewById(R.id.display_time_current_performance)
        textSpeed = view.findViewById(R.id.display_speed_current_performance)

        // update the value of the different text views
        pathDrawingModel.run.observe(viewLifecycleOwner) {
            textDistance.text = getStringDistance(run?.getDistance() ?: it.getDistance())
            textTime.text = getStringDuration(run?.getDuration() ?: it.getDuration())
            if ((run?.getDuration() ?: it.getDuration()) == 0L) {
                textSpeed.text = getStringSpeed(0.0)
            } else {
                textSpeed.text = getStringSpeed(run?.getAverageSpeed() ?: it.getAverageSpeed())
            }
        }
    }
}
