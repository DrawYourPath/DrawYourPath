package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.Utils.drawyourpath.Utils.getStringDistance
import com.epfl.Utils.drawyourpath.Utils.getStringDuration
import com.epfl.Utils.drawyourpath.Utils.getStringSpeed
import com.epfl.Utils.drawyourpath.Utils.getStringTimeStartEnd
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run

/**
 * Fragment used to displayed the deatil performance of a user during a run(time, distance, average speed, start time, end time, time for 1 km, calories).
 * @param run that contains the performance data
 */
class PathDrawingDetailPerformanceFragment(private val run: Run) : Fragment(R.layout.fragment_path_drawing_detail_performance) {
    private lateinit var textTime: TextView
    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private lateinit var textDistance: TextView
    private lateinit var textTimePerKm: TextView
    private lateinit var textSpeed: TextView
    private lateinit var textCalories: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initiate the different text view
        textTime = view.findViewById(R.id.display_time_detail_performance)
        textStartTime = view.findViewById(R.id.display_start_time_detail_performance)
        textEndTime = view.findViewById(R.id.display_end_time_detail_performance)
        textDistance = view.findViewById(R.id.display_distance_detail_performance)
        textTimePerKm = view.findViewById(R.id.display_time_km_detail_performance)
        textSpeed = view.findViewById(R.id.display_speed_detail_performance)
        textCalories = view.findViewById(R.id.display_calories_detail_performance)

        // update the value of the different text views
        textTime.text = getStringDuration(run.getDuration())
        textStartTime.text = getStringTimeStartEnd(run.getStartTime())
        textEndTime.text = getStringTimeStartEnd(run.getEndTime())
        textDistance.text = getStringDistance(run.getDistance())
        val maxTime = 24 * 60 * 60
        var time1km: Long = run.getTimeForOneKilometer()
        if (run.getTimeForOneKilometer() >= maxTime) { time1km = (maxTime - 1).toLong() }
        textTimePerKm.text = getStringDuration(time1km as Long)
        textSpeed.text = getStringSpeed(run.getAverageSpeed())
        textCalories.text = run.getCalories().toString()
    }
}
