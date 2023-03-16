package com.epfl.drawyourpath.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R


/**
 * used in a recycler view to display the [Tournament]
 */
class TournamentViewAdapter(private val tournaments: List<Tournament>) :
    RecyclerView.Adapter<TournamentViewAdapter.ViewHolder>() {

    /**
     * Custom view holder using a custom layout for tournaments
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
        viewHolder.name.text = "${tournaments[position].name}"
        viewHolder.description.text = "${tournaments[position].description}"
        viewHolder.date.text = "${tournaments[position].getStartOrEndDate()}"
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = tournaments.count()

}
