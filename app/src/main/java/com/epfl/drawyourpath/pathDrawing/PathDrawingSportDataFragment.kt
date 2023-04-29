package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run
import java.text.DecimalFormat
import java.time.Duration
import kotlin.math.roundToInt

class PathDrawingSportDataFragment(private val run: Run) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_sport_data, container, false)
    }
    private lateinit var textSpeed: TextView
    private lateinit var textDistance: TextView
    private lateinit var textTime: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initiate the different text view
        textDistance = view.findViewById(R.id.display_distance)
        textTime = view.findViewById(R.id.display_time)
        textSpeed = view.findViewById(R.id.display_speed)

        //update the value of the different text views
        editDisplayedCurrentSpeed()
        editDisplayedTime()
        editDisplayedDistance()
    }

    /**
     * Helper function to setup the distance displayed to the user(in km)
     */
    private fun editDisplayedDistance(){
        //convert m to km
        val roundDistance: Double = (run.getDistance()/10.0).roundToInt()/100.0
        textDistance.text = roundDistance.toString()
    }

    /**
     * Helper function to setup the time displayed to the user
     */
    private fun editDisplayedTime(){
        val duration = Duration.ofSeconds(run.getDuration())
        val hours : Int = duration.toHours().toInt()
        val hoursStr: String = if(hours==0) "00" else if(hours<10) "0$hours" else hours.toString()
        val minutes: Int = duration.toMinutes().toInt() - hours * 60
        val minutesStr: String = if(minutes==0) "00" else if(minutes<10) "0$minutes" else minutes.toString()
        val seconds: Int = duration.seconds.toInt() - 3600*hours - 60*minutes
        val secondsStr: String = if(seconds==0) "00" else if(seconds<10) "0$seconds" else seconds.toString()

        textTime.text = "$hoursStr:$minutesStr:$secondsStr"
    }

    /**
     * Helper function to setup the current speed displayed to the user
     */
    private fun editDisplayedCurrentSpeed(){
        val roundSpeed: Double = (run.getAverageSpeed() * 100.0).roundToInt() / 100.0
        textSpeed.text = roundSpeed.toString()
    }

}