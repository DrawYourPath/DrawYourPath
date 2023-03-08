package com.github.drawyourpath.bootcamp.challengeActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TournamentViewAdapter(private val tournaments: List<Tournament>) : RecyclerView.Adapter<TournamentViewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text1: TextView
        val text2: TextView

        init {
            // Define click listener for the ViewHolder's View
            text1 = view.findViewById(android.R.id.text1)
            text2 = view.findViewById(android.R.id.text2)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(android.R.layout.simple_list_item_2, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.text1.text = "${tournaments[position].name}"
        viewHolder.text2.text = "${tournaments[position].description}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = tournaments.count()

}
