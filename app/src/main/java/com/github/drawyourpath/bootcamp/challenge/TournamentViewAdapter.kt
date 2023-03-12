package com.github.drawyourpath.bootcamp.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R


class TournamentViewAdapter(private val tournaments: List<Tournament>) : RecyclerView.Adapter<TournamentViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val description: TextView
        val date: TextView

        init {
            // Define click listener for the ViewHolder's View
            name = view.findViewById(R.id.tournament_name_display_text)
            description = view.findViewById(R.id.tournament_description_display_text)
            date = view.findViewById(R.id.tournament_date_display_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_tournament, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = "${tournaments[position].name}"
        viewHolder.description.text = "${tournaments[position].description}"
        viewHolder.date.text = "${tournaments[position].getStartOrEndDate()}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = tournaments.count()

}
