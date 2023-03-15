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
import java.lang.Integer.min


/**
 * used in a recycler view to display the [DailyGoal]
 */
class DailyGoalViewAdapter(private val dailyGoal: DailyGoal) :
    RecyclerView.Adapter<DailyGoalViewAdapter.ViewHolder>() {

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

        viewHolder.text.text = dailyGoal.getGoalUnit(position)
        viewHolder.editText.setText(dailyGoal.getGoalToDouble(position).toInt().toString())

        viewHolder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    dailyGoal.updateGoal(s.toString(), viewHolder.adapterPosition)
                    val currentProgress =
                        dailyGoal.getProgressToDouble(viewHolder.adapterPosition)
                    val goal = dailyGoal.getGoalToDouble(viewHolder.adapterPosition)

                    viewHolder.progressText.text = "$currentProgress/$goal"
                    viewHolder.progressBar.max = goal.toInt()
                    viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        val currentProgress = dailyGoal.getProgressToDouble(position)
        val goal = dailyGoal.getGoalToDouble(position)

        viewHolder.progressText.text = "$currentProgress/$goal"
        viewHolder.progressBar.max = goal.toInt()
        viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())

    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = dailyGoal.count()

}
