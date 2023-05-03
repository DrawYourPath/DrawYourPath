package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * This fragment is used to display a countdown to the user(3,2,1,GO).
 */
class PathDrawingCountDownFragment : Fragment(R.layout.fragment_path_drawing_countdown) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // retrieve the textview to display the time remaining
        val countDownText: TextView = view.findViewById(R.id.countdown_text)

        // create the countdown timer of 4 seconds with a step of 1 second(4 seconds because 4 is not visible on the view at the beginning)
        object : CountDownTimer(4000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished) / 1000
                countDownText.text = if (seconds == 0L) "GO !" else seconds.toString()
            }

            override fun onFinish() {
                if (activity != null) {
                    displayedMainDrawFragment()
                }
            }
        }.start()
    }

    /**
     * Helper function to displayed the main draw fragment to begin to draw a path
     */
    private fun displayedMainDrawFragment(){
        //initial run
        val initRun = Run(path = Path(), startTime = getCurrentDateTimeInEpochSeconds(), endTime = getCurrentDateTimeInEpochSeconds()+1)
        // lunch the fragment to draw a path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_activity_content, PathDrawingMainFragment(isDrawing = true, run = initRun)).commit()
    }
}