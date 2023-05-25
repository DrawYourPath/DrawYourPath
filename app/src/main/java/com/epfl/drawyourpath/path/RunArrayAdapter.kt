package com.epfl.drawyourpath.path

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng

/**
 * this class is used in the spinner to display a dropdown list of run to select from
 * @param context the context
 * @param runs the runs to display
 * @param resource the resource layout
 */
class RunArrayAdapter(context: Context, runs: MutableList<Run>, @LayoutRes val resource: Int = R.layout.past_run) : ArrayAdapter<Run>(context, resource, runs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val viewHolder = ViewHolder(view)

        val run = getItem(position) ?: Run(Path(), 0, 0, 0)

        // TODO: will be refactor with the creation of a bitmap directly with the run coordinates

        val runCoordinates: List<LatLng> = run.getPath().getPoints().flatten() // Get the coordinates for this specific run
        // Use the utility function to load the map image
        Utils.loadMapImage(context, runCoordinates, viewHolder.mapImageView, run)

        // Set the data to the view items in the layout
        // holder.mapImageView.setImageResource(run.mapImage)
        viewHolder.dateTextView.text = run.getDate()
        viewHolder.distanceTextView.text = context.getString(R.string.display_distance).format(Utils.getStringDistance(run.getDistance()))
        viewHolder.timeTakenTextView.text = context.getString(R.string.display_duration).format(Utils.getStringDuration(run.getDuration()))
        viewHolder.shapeRecognizedTextView.text = context.getString(R.string.display_shape).format(run.predictedShape)
        viewHolder.shapeScoreTextView.text = context.getString(R.string.display_score).format(run.similarityScore)

        return view
    }

    inner class ViewHolder(view: View) {
        val mapImageView: ImageView = view.findViewById(R.id.mapImageView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = view.findViewById(R.id.distanceTextView)
        val timeTakenTextView: TextView = view.findViewById(R.id.timeTakenTextView)
        val shapeRecognizedTextView: TextView = view.findViewById(R.id.shapeTextView)
        val shapeScoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }
}
