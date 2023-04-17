package com.epfl.drawyourpath.challenge

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val setDistanceGoal: (distance: Double) -> CompletableFuture<Boolean>,
    private val setTimeGoal: (time: Double) -> CompletableFuture<Boolean>,
    private val setPathGoal: (path: Int) -> CompletableFuture<Boolean>
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

        init {
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

        viewHolder.text.text = getGoalUnit(goalPos)
        viewHolder.editText.setText(getGoalToDouble(dailyGoal, goalPos).toInt().toString())

        displayGoal(viewHolder, goalPos)

        viewHolder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    //TODO auto update needs fix
                    //updateGoal(s.toString(), goalPos)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
     * display the goal to the view
     *
     * @param viewHolder the view of where the items are
     * @param position the position inside the RecyclerView
     */
    private fun displayGoal(viewHolder: ViewHolder, position: GoalPos) {
        val currentProgress = getProgressToDouble(dailyGoal, position)
        val goal = getGoalToDouble(dailyGoal, position)

        viewHolder.progressText.text = "$currentProgress/$goal"
        viewHolder.progressBar.max = goal.toInt()
        viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())
    }

    /**
     * get the unit associated with the goal
     * @param pos the position of the goal
     *
     * @return the associated unit
     */
    private fun getGoalUnit(pos: GoalPos): String {
        return when (pos) {
            GoalPos.DISTANCE -> "kilometers"
            GoalPos.TIME -> "minutes"
            GoalPos.PATH -> "paths"
        }
    }

    /**
     * update the DailyGoal associated to pos with a new value
     *
     * @param value the value
     * @param pos the position of the goal
     */
    private fun updateGoal(value: String, pos: GoalPos) {
        val doubleValue = value.toDoubleOrNull()

        if ((doubleValue == null) || (doubleValue <= 0)) {
            return
        }
        when (pos) {
            GoalPos.DISTANCE -> setDistanceGoal(doubleValue)
            GoalPos.TIME -> setTimeGoal(doubleValue)
            GoalPos.PATH -> setPathGoal(doubleValue.toInt())
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
            GoalPos.TIME -> goal.timeInMinutesGoal
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
            GoalPos.TIME -> goal.timeInMinutesProgress
            GoalPos.PATH -> goal.nbOfPathsProgress.toDouble()
        }
    }

    private enum class GoalPos {
        DISTANCE, TIME, PATH;
    }

}
