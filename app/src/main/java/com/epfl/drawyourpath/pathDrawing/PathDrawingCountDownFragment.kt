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
import com.epfl.drawyourpath.utils.Utils.getCurrentDateTimeInEpochSeconds

/**
 * This fragment is used to display a countdown to the user(3,2,1,GO).
 * @param countdownDuration the duration of the countdown in seconds(default is 4s)
 */
class PathDrawingCountDownFragment(private val countdownDuration: Long = 4) : Fragment(R.layout.fragment_path_drawing_countdown) {
    private lateinit var countDownText: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // retrieve the textview to display the time remaining
        countDownText = view.findViewById(R.id.countdown_text)

        // create the countdown timer of 4 seconds with a step of 1 second(4 seconds because 4 is not visible on the view at the beginning)
        createCountDown(countdownDuration)
    }

    /**
     * Helper function to create and lunch a countdown object
     * @param seconds duration in seconds of the countdown
     */
    private fun createCountDown(seconds: Long) {
        object : CountDownTimer(1000 * seconds, 100) {
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
    private fun displayedMainDrawFragment() {
        // initial run
        val initRun = Run(path = Path(), startTime = getCurrentDateTimeInEpochSeconds(), endTime = getCurrentDateTimeInEpochSeconds() + 1)
        // lunch the fragment to draw a path
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.path_drawing_activity_content, PathDrawingMainFragment(isDrawing = true, run = initRun)).commit()
    }
}
