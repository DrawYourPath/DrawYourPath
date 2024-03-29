package com.epfl.drawyourpath.community

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TournamentCreationFragment : Fragment(R.layout.fragment_tournament_creation) {

    private val userModel: UserModelCached by activityViewModels()
    private lateinit var tournament: TournamentModel

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var startDate: TextView
    private lateinit var startTime: TextView
    private lateinit var endDate: TextView
    private lateinit var endTime: TextView

    // TODO add visibility to tournament
    private lateinit var visibility: RadioGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tournament = ViewModelProvider(
            requireActivity(),
            TournamentModel.getFactory(userModel.getDatabase(), userModel.getUserId()!!),
        )[TournamentModel::class.java]

        initVariable(view)

        setDefaultTimeAndDate()

        createBackButton(view)

        createVisibilityRadioButton(view)

        createDateAndTime(startDate, startTime)

        createDateAndTime(endDate, endTime)

        view.findViewById<Button>(R.id.tournament_creation_create_button).setOnClickListener {
            onCreateTournamentClicked(view)
        }
    }

    /**
     * Handles the logic when the "Create Tournament" button is pressed.
     */
    private fun onCreateTournamentClicked(view: View) {
        // check that the parameters are entered correctly
        val newTournamentParameters =
            checkTournamentConstraints(view) ?: return

        tournament.createTournament(newTournamentParameters, requireActivity())

        // get back to community fragment without waiting for database
        requireActivity().supportFragmentManager.popBackStack()
    }

    /**
     * check the constraints on the tournament
     *
     * @return the tournament if the constraints are satisfied else null
     */
    private fun checkTournamentConstraints(view: View): Tournament.TournamentParameters? {
        val titleError = view.findViewById<TextView>(R.id.tournament_creation_title_error)
        val descriptionError =
            view.findViewById<TextView>(R.id.tournament_creation_description_error)
        val startDateError = view.findViewById<TextView>(R.id.tournament_creation_start_date_error)
        val endDateError = view.findViewById<TextView>(R.id.tournament_creation_end_date_error)
        val visibilityError = view.findViewById<TextView>(R.id.tournament_creation_visibility_error)

        val tournamentTitle = title.text
        val tournamentDescription = description.text
        val tournamentStartDate = LocalDateTime.of(getDate(startDate), getTime(startTime))
        val tournamentEndDate = LocalDateTime.of(getDate(endDate), getTime(endTime))
        val tournamentVisibility = visibility.checkedRadioButtonId

        var error = false

        // check title
        error = error or checkConstraint(
            tournamentTitle.isBlank(),
            getString(R.string.tournament_creation_title_error),
            titleError,
        )
        // check description
        error = error or checkConstraint(
            tournamentDescription.isBlank(),
            getString(R.string.tournament_creation_description_error),
            descriptionError,
        )
        // check start date and time
        error = error or checkConstraint(
            tournamentStartDate < LocalDateTime.now().plus(MIN_START_TIME_INTERVAL),
            getString(R.string.tournament_creation_start_date_error),
            startDateError,
        )
        // check end date and time
        error = error or checkConstraint(
            tournamentEndDate < tournamentStartDate.plus(MIN_END_TIME_INTERVAL),
            getString(R.string.tournament_creation_end_date_error),
            endDateError,
        )
        // check visibility
        error = error or checkConstraint(
            tournamentVisibility == -1,
            getString(R.string.tournament_creation_visibility_error),
            visibilityError,
        )

        if (error) {
            return null
        }

        return Tournament.TournamentParameters(
            tournamentTitle.toString(),
            tournamentDescription.toString(),
            tournamentStartDate,
            tournamentEndDate,
            getVisibility(view.findViewById(tournamentVisibility)),
        )
    }

    /**
     * check if the constraint holds or not and show the error message accordingly
     *
     * @param isError the constraint
     * @param errorText the error message
     * @param error the text view used to show the error message
     * @return [isError]
     *
     */
    private fun checkConstraint(isError: Boolean, errorText: String, error: TextView): Boolean {
        if (isError) {
            error.text = errorText
            error.visibility = View.VISIBLE
        } else {
            error.visibility = View.GONE
        }
        return isError
    }

    /**
     * create the visibility radio button
     */
    private fun createVisibilityRadioButton(view: View) {
        setVisibility(0, view.findViewById(R.id.tournament_creation_visibility_item_0))
        setVisibility(1, view.findViewById(R.id.tournament_creation_visibility_item_1))
    }

    /**
     * get the visibility from the radio button
     *
     * @param radioButton the radio button
     * @return [Tournament.Visibility] the visibility
     *
     */
    private fun getVisibility(radioButton: RadioButton): Tournament.Visibility {
        return Tournament.Visibility.valueOf(
            radioButton.text.toString().uppercase().replace(" ", "_"),
        )
    }

    /**
     * set the visibility name to the radio button
     *
     * @param index the ordinal of the [Tournament.Visibility] enum
     * @param radioButton the radio button
     *
     */
    private fun setVisibility(index: Int, radioButton: RadioButton) {
        radioButton.text = Tournament.Visibility.values()[index].name.lowercase().replace("_", " ")
            .replaceFirstChar { c -> c.uppercaseChar() }
    }

    /**
     * initialize the variable
     */
    private fun initVariable(view: View) {
        title = view.findViewById(R.id.tournament_creation_title)
        description = view.findViewById(R.id.tournament_creation_description)
        startDate = view.findViewById(R.id.tournament_creation_start_date)
        startTime = view.findViewById(R.id.tournament_creation_start_time)
        endDate = view.findViewById(R.id.tournament_creation_end_date)
        endTime = view.findViewById(R.id.tournament_creation_end_time)
        visibility = view.findViewById(R.id.tournament_creation_visibility)
    }

    /**
     * set the start date/time and the end date/date to default values
     */
    private fun setDefaultTimeAndDate() {
        setDate(DEFAULT_START_DATE, startDate)
        setTime(DEFAULT_START_TIME, startTime)
        setDate(DEFAULT_END_DATE, endDate)
        setTime(DEFAULT_END_TIME, endTime)
    }

    /**
     * create the date and time
     * @param date the start or end date
     * @param time the start or end time
     */
    private fun createDateAndTime(date: TextView, time: TextView) {
        date.setOnClickListener {
            DatePickerFragment(date).show(activity?.supportFragmentManager!!, "startDatePicker")
        }
        time.setOnClickListener {
            TimePickerFragment(time).show(activity?.supportFragmentManager!!, "startTimePicker")
        }
    }

    /**
     * create the back button
     */
    private fun createBackButton(view: View) {
        val backButton = view.findViewById<ImageButton>(R.id.tournament_creation_back_button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val DATE_SEPARATOR = " / "
        private const val TIME_SEPARATOR = " : "
        private val DEFAULT_START_DATE = LocalDate.now()
        private val DEFAULT_START_TIME =
            LocalTime.now().plusHours(2L).minusMinutes(LocalTime.now().minute.toLong())
        private val DEFAULT_END_DATE = DEFAULT_START_DATE.plusWeeks(1L)
        private val DEFAULT_END_TIME = DEFAULT_START_TIME
        val MIN_START_TIME_INTERVAL: Duration = Duration.of(1L, ChronoUnit.HOURS)
        val MIN_END_TIME_INTERVAL: Duration = Duration.of(1L, ChronoUnit.DAYS)

        /**
         * get the date from the date text view
         */
        private fun getDate(date: TextView): LocalDate {
            val texts = date.text.split(DATE_SEPARATOR)
            return LocalDate.of(texts[2].toInt(), texts[1].toInt(), texts[0].toInt())
        }

        /**
         * get the time from the time text view
         */
        private fun getTime(time: TextView): LocalTime {
            val texts = time.text.split(TIME_SEPARATOR)
            return LocalTime.of(texts[0].toInt(), texts[1].toInt())
        }

        /**
         * set the date to the date text
         */
        private fun setDate(date: LocalDate, text: TextView) {
            text.text = buildString {
                append("%02d".format(date.dayOfMonth))
                append(DATE_SEPARATOR)
                append("%02d".format(date.monthValue))
                append(DATE_SEPARATOR)
                append(date.year)
            }
        }

        /**
         * set the time to the time text
         */
        private fun setTime(time: LocalTime, text: TextView) {
            text.text = buildString {
                append("%02d".format(time.hour))
                append(TIME_SEPARATOR)
                append("%02d".format(time.minute))
            }
        }
    }

    /**
     * create a datePicker
     */
    class DatePickerFragment(text: TextView) :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {
        private val text: TextView

        init {
            this.text = text
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // create a new instance of date picker

            val date = getDate(text)
            return DatePickerDialog(
                requireContext(),
                this,
                date.year,
                date.monthValue - 1,
                date.dayOfMonth,
            )
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            setDate(LocalDate.of(year, month + 1, day), text)
        }
    }

    /**
     * create a time picker
     */
    class TimePickerFragment(text: TextView) :
        DialogFragment(),
        TimePickerDialog.OnTimeSetListener {
        private val text: TextView

        init {
            this.text = text
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // create a new instance of time picker

            val time = getTime(text)
            return TimePickerDialog(
                requireContext(),
                this,
                time.hour,
                time.minute,
                true,
            )
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            setTime(LocalTime.of(hourOfDay, minute), text)
        }
    }
}
