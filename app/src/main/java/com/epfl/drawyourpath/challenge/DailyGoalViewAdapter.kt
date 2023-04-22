package com.epfl.drawyourpath.challenge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.lang.Integer.min
import java.util.concurrent.CompletableFuture

/**
 * used in a recycler view to display the [DailyGoal]
 */
class DailyGoalViewAdapter(
    private val updateDistanceGoal: (distance: Double) -> CompletableFuture<Unit>,
    private val updateTimeGoal: (time: Double) -> CompletableFuture<Unit>,
    private val updatePathGoal: (path: Int) -> CompletableFuture<Unit>,
) :
    RecyclerView.Adapter<DailyGoalViewAdapter.ViewHolder>() {

    private var dailyGoal = DailyGoal(0.0, 0.0, 0)

    /**
     * Custom view holder using a custom layout for DailyGoal
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView
        val editText: EditText
        val progressText: TextView
        val progressBar: ProgressBar
        val view: View

        init {
            this.view = view
            text = view.findViewById(R.id.goal_display_text)
            editText = view.findViewById(R.id.goal_display_edit_text)
            progressText = view.findViewById(R.id.goal_progress_text)
            progressBar = view.findViewById(R.id.goal_progress_bar)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_daily_goal, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val goalPos = GoalPos.values()[viewHolder.adapterPosition]

        viewHolder.text.text = getGoalUnit(goalPos, viewHolder.view.context)
        setEditText(viewHolder, goalPos)

        displayGoal(viewHolder, goalPos, viewHolder.view.context)

        viewHolder.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val goal = checkGoalDouble(textView.text.toString())
                setEditText(viewHolder, goalPos)
                if (goal != null && goal != getGoalToDouble(dailyGoal, goalPos)) {
                    updateGoal(goal, goalPos)
                }
                closeKeyboard(viewHolder.view)
                true
            } else {
                false
            }
        }
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = 3

    /**
     * update the daily goal
     * @param dailyGoal the new daily goal
     */
    fun updateDailyGoal(dailyGoal: DailyGoal) {
        this.dailyGoal = dailyGoal
        notifyItemRangeChanged(0, itemCount)
    }

    /**
     * close the keyboard
     * @param view
     */
    private fun closeKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * set the edit text to DailyGoal
     * @param viewHolder the view of where the items are
     * @param position the position inside the RecyclerView
     */
    private fun setEditText(viewHolder: ViewHolder, position: GoalPos) {
        viewHolder.editText.setText(getGoalToDouble(dailyGoal, position).toInt().toString())
    }

    /**
     * display the goal to the view
     *
     * @param viewHolder the view of where the items are
     * @param position the position inside the RecyclerView
     * @param context the context of the view (to get the string resource)
     */
    private fun displayGoal(viewHolder: ViewHolder, position: GoalPos, context: Context) {
        val currentProgress = getProgressToDouble(dailyGoal, position)
        val goal = getGoalToDouble(dailyGoal, position)

        viewHolder.progressText.text = getProgressText(position, context).format(currentProgress, goal)
        viewHolder.progressBar.max = goal.toInt()
        viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())
    }

    /**
     * get the unit associated with the goal
     * @param pos the position of the goal
     * @param context the context of the view (to get the string resource)
     * @return the associated unit
     */
    private fun getGoalUnit(pos: GoalPos, context: Context): String {
        return when (pos) {
            GoalPos.DISTANCE -> context.getString(R.string.kilometers)
            GoalPos.TIME -> context.getString(R.string.minutes)
            GoalPos.PATH -> context.getString(R.string.paths)
        }
    }

    /**
     * get the progress text associated with the goal
     * @param pos the position of the goal
     * @param context the context of the view (to get the string resource)
     */
    private fun getProgressText(pos: GoalPos, context: Context): String {
        return when (pos) {
            GoalPos.DISTANCE -> context.getString(R.string.progress_over_goal)
            GoalPos.TIME -> context.getString(R.string.progress_over_goal)
            GoalPos.PATH -> context.getString(R.string.progress_over_goal_path)
        }
    }

    /**
     * check that the goal is in the correct format and return null otherwise
     * @param value the value to check
     * @return the value in the correct format or null
     */
    private fun checkGoalDouble(value: String): Double? {
        val intValue = value.toIntOrNull()

        if ((intValue == null) || (intValue <= 0)) {
            return null
        }
        return intValue.toDouble()
    }

    /**
     * update the DailyGoal associated to pos with a new value
     *
     * @param value the value
     * @param pos the position of the goal
     */
    private fun updateGoal(value: Double, pos: GoalPos) {
        when (pos) {
            GoalPos.DISTANCE -> updateDistanceGoal(value)
            GoalPos.TIME -> updateTimeGoal(value)
            GoalPos.PATH -> updatePathGoal(value.toInt())
        }
    }

    /**
     * get the value of the DailyGoal associated to pos
     *
     * @param pos the position of the goal
     *
     * @return the value of the goal
     */
    private fun getGoalToDouble(goal: DailyGoal, pos: GoalPos): Double {
        return when (pos) {
            GoalPos.DISTANCE -> goal.distanceInKilometerGoal
            GoalPos.TIME -> goal.activityTimeInMinutesGoal
            GoalPos.PATH -> goal.nbOfPathsGoal.toDouble()
        }
    }

    /**
     * get the value of the current progress of the DailyGoal associated to pos
     *
     * @param pos the position of the goal
     *
     * @return the value of the progress
     */
    private fun getProgressToDouble(goal: DailyGoal, pos: GoalPos): Double {
        return when (pos) {
            GoalPos.DISTANCE -> goal.distanceInKilometerProgress
            GoalPos.TIME -> goal.activityTimeInMinutesProgress
            GoalPos.PATH -> goal.nbOfPathsProgress.toDouble()
        }
    }

    private enum class GoalPos {
        DISTANCE, TIME, PATH
    }
}
