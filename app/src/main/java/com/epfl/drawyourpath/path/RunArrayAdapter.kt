package com.epfl.drawyourpath.path

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bumptech.glide.Glide
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng

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
        val apiKey = "AIzaSyCE8covSYZE_sOv4Z-HaoljRlNOTV8cKRk"

        val staticMapUrl = Utils.getStaticMapUrl(runCoordinates, apiKey)

        // Load the image using Glide
        Glide.with(context)
            .load(staticMapUrl)
            .placeholder(R.drawable.map_loading_placeholder) // Set a placeholder image while loading
            .into(viewHolder.mapImageView)

        // Set the data to the view items in the layout
        // holder.mapImageView.setImageResource(run.mapImage)
        viewHolder.dateTextView.text = run.getDate()
        viewHolder.distanceTextView.text =
            "Distance: ${String.format("%.2f", run.getDistance() / 1000)} Km"
        viewHolder.timeTakenTextView.text = "Time taken: ${run.getDuration() / 60} minutes"

        return view
    }

    inner class ViewHolder(view: View) {
        val mapImageView: ImageView = view.findViewById(R.id.mapImageView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = view.findViewById(R.id.distanceTextView)
        val timeTakenTextView: TextView = view.findViewById(R.id.timeTakenTextView)
    }
}
