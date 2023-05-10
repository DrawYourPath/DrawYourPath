package com.epfl.drawyourpath.challenge.milestone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R

/**
 * used in a recycler view to display the [Milestone]
 */
class MilestoneViewAdapter : RecyclerView.Adapter<MilestoneViewAdapter.ViewHolder>() {

    private var milestones: List<Milestone> = listOf()

    /**
     * Custom view holder using a custom layout for trophies
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val description: TextView
        val date: TextView
        val image: ImageView
        val context: Context

        init {
            name = view.findViewById(R.id.milestone_display_text)
            description = view.findViewById(R.id.milestone_description_display_text)
            date = view.findViewById(R.id.milestone_date_display_text)
            image = view.findViewById(R.id.milestone_image)
            context = view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_milestone, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = milestones[position].name
        viewHolder.description.text = milestones[position].description
        viewHolder.date.text = viewHolder.context.getString(R.string.acquired).format(milestones[position].dateAsString)
        viewHolder.image.setImageResource(milestones[position].drawable)
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = milestones.count()

    fun update(milestones: List<Milestone>) {
        this.milestones = milestones
        notifyItemRangeChanged(0, milestones.size)
    }
}
