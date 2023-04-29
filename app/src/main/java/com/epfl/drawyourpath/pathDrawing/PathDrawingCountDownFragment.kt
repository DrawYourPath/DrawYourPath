package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R

class PathDrawingCountDownFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_count_down, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve the textview to display the time remaining
        val countDownText: TextView = view.findViewById(R.id.countdown_text)

        //create the countdown timer of 4 seconds with a step of 1 second(4 seconds because 4 is not visible on the view at the beginning)
        object : CountDownTimer(4000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished) / 1000
                countDownText.text = if (seconds == 0L) "GO" else seconds.toString()
            }

            override fun onFinish() {
                //lunch the fragment to draw a path
                val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.contentFragmentPathDrawing, PathDrawingMainFragment(isDrawing = true))
                fragTransaction.commit()
            }
        }.start()
    }
}