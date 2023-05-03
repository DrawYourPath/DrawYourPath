package com.epfl.drawyourpath.community

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.Auth
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.mainpage.fragments.CommunityFragment
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TournamentCreationFragment : Fragment(R.layout.fragment_tournament_creation) {

    //Keep track of the context in futures
    private lateinit var mActivity: Activity

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var startDate: TextView
    private lateinit var startTime: TextView
    private lateinit var endDate: TextView
    private lateinit var endTime: TextView

    // TODO add visibility to tournament
    private lateinit var visibility: RadioGroup

    //data class used when the user has chosen the parameters of the tournament.
    data class TournamentParameters(
        val name: String,
        val description: String,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        val visibility: Tournament.Visibility
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariable(view)

        setDefaultTimeAndDate()

        createBackButton(view)

        createVisibilityRadioButton(view)

        createDateAndTime(startDate, startTime)

        createDateAndTime(endDate, endTime)

        createCreateButton(view)
    }

    /**
     * create the create button
     */
    private fun createCreateButton(view: View) {
        val createButton = view.findViewById<Button>(R.id.tournament_creation_create_button)
        createButton.setOnClickListener {

            // check that the parameters are entered correctly
            val newTournamentParameters =
                checkTournamentConstraints(view) ?: return@setOnClickListener

            // get the auth and database (could be mock)
            val database = getDatabase()
            val auth = getAuth()

            // get the id of the creator of the tournament from auth
            val creatorId = auth.getUser()?.getUid()
            if (creatorId == null) {
                Toast.makeText(mActivity, "No user logged in!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // get a uid for the tournament from database (client side)
            val id = database.getTournamentUID()
            if (id == null) {
                Toast.makeText(
                    mActivity, "Can't get a tournament id!", Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // ask to store the tournament in the database and handle exceptions
            storeNewTournament(newTournamentParameters, id, creatorId, database)

            // get back to community fragment without waiting for database
            replaceFragment<CommunityFragment>()

        }
    }

    /**
     * Helper function that returns a database based on the arguments passed to
     */
    private fun getDatabase(): Database {
        return if (arguments?.getBoolean("USE_WORKING_MOCK_DB", false) == true) {
            MockDatabase()
        } else if (arguments?.getBoolean("USE_FAILING_MOCK_DB", false) == true) {
            MockNonWorkingDatabase()
        } else {
            FirebaseDatabase()
        }
    }

    private fun getAuth(): Auth {
        return if (arguments?.getBoolean("USE_WORKING_MOCK_AUTH", false) == true) {
            MockAuth(forceSigned = true)
        } else if (arguments?.getBoolean("USE_FAILING_MOCK_AUTH", false) == true) {
            MockAuth()
        } else {
            FirebaseAuth()
        }
    }

    /**
     * Helper function to store the newly created tournament asynchronously, displays toasts
     * to notify of the success of failure of the operation.
     *
     * @param params the TournamentParameters data class containing the choices of the creator
     * @param id the unique id of the tournament (given by the database)
     * @param creatorId the id of the user creating the tournament
     * @param db the database in which we store the tournament
     */
    private fun storeNewTournament(
        params: TournamentParameters,
        id: String,
        creatorId: String,
        db: Database
    ) {
        db.addTournament(
            Tournament(
                id = id,
                name = params.name,
                description = params.description,
                creatorId = creatorId,
                startDate = params.startDate,
                endDate = params.endDate,
                visibility = params.visibility
            )
        ).whenComplete { _, exception ->
            // Display toasts when complete. Note that it does not complete while offline (but the tournament
            // is stored when the user gets back online)
            if (exception != null) {
                Toast.makeText(
                    mActivity,
                    "Operation failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(mActivity, "Tournament created!", Toast.LENGTH_LONG).show()
            }

        }
    }


    /**
     * check the constraints on the tournament
     *
     * @return the tournament if the constraints are satisfied else null
     */
    private fun checkTournamentConstraints(view: View): TournamentParameters? {
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
            titleError
        )
        // check description
        error = error or checkConstraint(
            tournamentDescription.isBlank(),
            getString(R.string.tournament_creation_description_error),
            descriptionError
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
            visibilityError
        )

        if (error) {
            return null
        }

        return TournamentParameters(
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
            radioButton.text.toString().uppercase().replace(" ", "_")
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
    class DatePickerFragment(text: TextView) : DialogFragment(),
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
    class TimePickerFragment(text: TextView) : DialogFragment(),
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
