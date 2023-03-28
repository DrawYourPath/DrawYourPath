package com.epfl.drawyourpath.community

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.CommunityFragment
import java.time.LocalDate
import java.time.LocalTime

class TournamentCreationFragment : Fragment(R.layout.fragment_tournament_creation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createBackButton(view)

        createStartDateAndTime(view)

    }

    private fun createStartDateAndTime(view: View) {
        val startDate = view.findViewById<TextView>(R.id.tournament_creation_start_date)
        startDate.setOnClickListener {
            DatePickerFragment(startDate).show(activity?.supportFragmentManager!!, "startDatePicker")
        }
        val startTime = view.findViewById<TextView>(R.id.tournament_creation_start_time)
        startTime.setOnClickListener {
            TimePickerFragment(startTime).show(activity?.supportFragmentManager!!, "startTimePicker")
        }
    }


    /**
     * create the back button
     */
    private fun createBackButton(view: View) {
        val backButton = view.findViewById<ImageButton>(R.id.tournament_creation_back_button)
        backButton.setOnClickListener {
            replaceFragment<CommunityFragment>()
        }
    }

    /**
     * replace this fragment by another one
     */
    private inline fun <reified F : Fragment> replaceFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragmentContainerView)
        }
    }

    /**
     * create a datePicker
     */
    class DatePickerFragment(text: TextView) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private val text: TextView
        var dateSelected = LocalDate.now()

        init {
            this.text = text
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //create a new instance of date picker
            //use the current date as the default date in the picker
            return DatePickerDialog(
                requireContext(),
                this,
                dateSelected.year,
                dateSelected.monthValue - 1,
                dateSelected.dayOfMonth
            )
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            text.text = buildString {
                append(day)
                append(" / ")
                append(month + 1)
                append(" / ")
                append(year)
            }
            dateSelected = LocalDate.of(year, month + 1, day)
        }
    }

    /**
     * create a time picker
     */
    class TimePickerFragment(text: TextView) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        private val text: TextView
        var timeSelected = LocalTime.now()

        init {
            this.text = text
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //create a new instance of time picker
            //use the current time + 1 hour and 0 minute as the default time in the picker
            return TimePickerDialog(
                requireContext(),
                this,
                (timeSelected.hour + 1) % 24,
                0,
                true
            )
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            text.text = buildString {
                append(hourOfDay)
                append(" : ")
                append(minute)
            }
            timeSelected = LocalTime.of(hourOfDay, minute)
        }
    }

}