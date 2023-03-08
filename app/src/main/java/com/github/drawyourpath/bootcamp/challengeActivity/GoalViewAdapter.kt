package com.github.drawyourpath.bootcamp.challengeActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R


class GoalViewAdapter(private val finalGoal: Goal, private val current: Goal) : RecyclerView.Adapter<GoalViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView
        val progressText: TextView
        val progressBar: ProgressBar

        init {
            // Define click listener for the ViewHolder's View
            text = view.findViewById(R.id.goal_display_text)
            progressText = view.findViewById(R.id.goal_progress_text)
            progressBar = view.findViewById(R.id.goal_progress_bar)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.goal_display_list, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.text.text = finalGoal.getToString(position)

        var currentProgress = current.getToDouble(position).toInt()
        val goal = finalGoal.getToDouble(position).toInt()
        if (currentProgress > goal) {
            currentProgress = goal
        }

        viewHolder.progressText.text = "$currentProgress/$goal"
        viewHolder.progressBar.max = goal
        viewHolder.progressBar.progress = currentProgress

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = finalGoal.count()

}
