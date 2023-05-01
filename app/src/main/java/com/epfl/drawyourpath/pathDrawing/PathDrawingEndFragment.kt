package com.epfl.drawyourpath.pathDrawing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.time.Duration
import kotlin.math.roundToInt

class PathDrawingEndFragment(private val run: Run) : Fragment() {
    private val userCached: UserModelCached by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_end, container, false)
    }
    private lateinit var textSpeed: TextView
    private lateinit var textDistance: TextView
    private lateinit var textTime: TextView
    private lateinit var textCalorie: TextView
    private lateinit var textTimePerKm: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initiate the different text view
        textDistance = view.findViewById(R.id.display_distance_end)
        textTime = view.findViewById(R.id.display_time_end)
        textSpeed = view.findViewById(R.id.display_speed_end)
        textCalorie = view.findViewById(R.id.display_calories_end)
        textTimePerKm = view.findViewById(R.id.time_perKm_end)

        // update the value of the different text views
        editDisplayedCurrentSpeed()
        editDisplayedTime()
        editDisplayedDistance()
        editDisplayedCalories()
        editDisplayedTimePerKm()

        // display the preview of the path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.content_end_preview, MapFragment(showCurrentPosition = false, path = run.getPath()))
        fragTransaction.commit()

        // return back to the menu add and save the path back clicking and the back to menu button
        val backToMenuButton: Button = view.findViewById(R.id.path_drawing_back_menu_button)
        backToMenuButton.setOnClickListener {
            // TODO:Save the path to the database
            val intent = Intent(activity, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    /**
     * Helper function to setup the distance displayed to the user(in km)
     */
    private fun editDisplayedDistance() {
        // convert m to km
        val roundDistance: Double = (run.getDistance() / 10.0).roundToInt() / 100.0
        textDistance.text = roundDistance.toString()
    }

    /**
     * Helper function to setup the calories displayed to the user(in km)
     */
    private fun editDisplayedCalories() {
        textCalorie.text = run.getCalories().toString()
    }

    /**
     * Helper function to setup the time per km displayed to the user
     */
    private fun editDisplayedTimePerKm() {
        val duration = Duration.ofSeconds(run.getTimeForOneKilometer())
        val minutes: Int = duration.toMinutes().toInt()
        val minutesStr: String = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes.toString()
        val seconds: Int = duration.seconds.toInt() - 60 * minutes
        val secondsStr: String = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds.toString()

        textTimePerKm.text = "$minutesStr:$secondsStr"
    }

    /**
     * Helper function to setup the current speed displayed to the user
     */
    private fun editDisplayedCurrentSpeed() {
        val roundSpeed: Double = (run.getAverageSpeed() * 100.0).roundToInt() / 100.0
        textSpeed.text = roundSpeed.toString()
    }

    /**
     * Helper function to setup the time displayed to the user
     */
    private fun editDisplayedTime() {
        val duration = Duration.ofSeconds(run.getDuration())
        val hours: Int = duration.toHours().toInt()
        val hoursStr: String = if (hours == 0) "00" else if (hours < 10) "0$hours" else hours.toString()
        val minutes: Int = duration.toMinutes().toInt() - hours * 60
        val minutesStr: String = if (minutes == 0) "00" else if (minutes < 10) "0$minutes" else minutes.toString()
        val seconds: Int = duration.seconds.toInt() - 3600 * hours - 60 * minutes
        val secondsStr: String = if (seconds == 0) "00" else if (seconds < 10) "0$seconds" else seconds.toString()

        textTime.text = "$hoursStr:$minutesStr:$secondsStr"
    }
}
