package com.github.drawyourpath.bootcamp.challenge

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R
import java.time.LocalDate


/**
 * used in a recycler view to display the [Trophy]
 */
class TrophyViewAdapter(private val trophies: List<Pair<Trophy, LocalDate>>) :
    RecyclerView.Adapter<TrophyViewAdapter.ViewHolder>() {

    /**
     * Custom view holder using a custom layout for trophies
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView
        val dateText: TextView
        val image: ImageView
        val assets: AssetManager

        init {
            text = view.findViewById(R.id.trophy_display_text)
            dateText = view.findViewById(R.id.trophy_date_display_text)
            image = view.findViewById(R.id.trophy_image)
            assets = view.context.assets
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

        viewHolder.text.text = trophies[position].first.trophyName
        viewHolder.dateText.text = "Acquired ${trophies[position].second.toString()}"
        try {
            val imageStream = viewHolder.assets.open(trophies[position].first.imagePath)
            viewHolder.image.setImageDrawable(Drawable.createFromStream(imageStream, null))
            imageStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = trophies.count()

}
