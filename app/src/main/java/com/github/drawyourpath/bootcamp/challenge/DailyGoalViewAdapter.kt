package com.github.drawyourpath.bootcamp.challenge

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R
import java.lang.Integer.min


class DailyGoalViewAdapter(private val finalDailyGoal: DailyGoal) :
    RecyclerView.Adapter<DailyGoalViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView
        val editText: EditText
        val progressText: TextView
        val progressBar: ProgressBar

        init {
            // Define click listener for the ViewHolder's View
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

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.text.text = finalDailyGoal.getGoalUnit(position)
        viewHolder.editText.setText(finalDailyGoal.getGoalToDouble(position).toInt().toString())

        viewHolder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    finalDailyGoal.updateGoal(s.toString(), viewHolder.adapterPosition)
                    val currentProgress = finalDailyGoal.getProgressToDouble(viewHolder.adapterPosition)
                    val goal = finalDailyGoal.getGoalToDouble(viewHolder.adapterPosition)

                    viewHolder.progressText.text = "$currentProgress/$goal"
                    viewHolder.progressBar.max = goal.toInt()
                    viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        val currentProgress = finalDailyGoal.getProgressToDouble(position)
        val goal = finalDailyGoal.getGoalToDouble(position)

        viewHolder.progressText.text = "$currentProgress/$goal"
        viewHolder.progressBar.max = goal.toInt()
        viewHolder.progressBar.progress = min(currentProgress.toInt(), goal.toInt())

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = finalDailyGoal.count()

}
