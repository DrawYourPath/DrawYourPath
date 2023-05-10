package com.epfl.drawyourpath.challenge.trophy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R

/**
 * used in a recycler view to display the [Trophy]
 */
class TrophyViewAdapter : RecyclerView.Adapter<TrophyViewAdapter.ViewHolder>() {

    private var trophies: List<Trophy> = listOf()

    /**
     * Custom view holder using a custom layout for tournaments
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val description: TextView
        val date: TextView
        val rank: TextView
        val image: ImageView
        val context: Context

        init {
            // Define click listener for the ViewHolder's View
            name = view.findViewById(R.id.trophy_name_display_text)
            description = view.findViewById(R.id.trophy_description_display_text)
            date = view.findViewById(R.id.trophy_date_display_text)
            rank = view.findViewById(R.id.trophy_rank_display_text)
            image = view.findViewById(R.id.trophy_image)
            context = view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_trophy, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = trophies[position].tournamentName
        viewHolder.description.text = trophies[position].tournamentDescription
        viewHolder.date.text = viewHolder.context.getString(R.string.acquired).format(trophies[position].dateAsString)
        viewHolder.rank.text = trophies[position].ranking.toString().let { "#$it" }
        viewHolder.image.setImageResource(trophies[position].drawable)
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = trophies.count()

    fun update(trophies: List<Trophy>) {
        this.trophies = trophies
        notifyItemRangeChanged(0, trophies.size)
    }

}