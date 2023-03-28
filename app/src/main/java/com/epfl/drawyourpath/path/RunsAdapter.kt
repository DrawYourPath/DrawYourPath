package com.github.drawyourpath.path

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.github.drawyourpath.bootcamp.path.Run

/**
 * This class is the adapter for the RecyclerView that displays the list of runs.
 */
class RunsAdapter(private val runs: List<Run>) : RecyclerView.Adapter<RunsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.past_run, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = runs[position]

        // Set the data to the view items in the layout
        //holder.mapImageView.setImageResource(run.mapImage)
        holder.dateTextView.text = run.getDate()
        holder.distanceTextView.text =
            "Distance: ${String.format("%.2f", run.getDistance() / 1000)} Km"
        holder.timeTakenTextView.text = "Time taken: ${run.getDuration()} minutes"
        holder.calorieTextView.text = "Calories burned: ${run.getCalories()} kcal"
        holder.averageSpeedTextView.text =
            "Speed: ${String.format("%.2f", run.getAverageSpeed())} m/s"
    }

    override fun getItemCount(): Int {
        return runs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mapImageView: ImageView = itemView.findViewById(R.id.mapImageView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        val timeTakenTextView: TextView = itemView.findViewById(R.id.timeTakenTextView)
        val calorieTextView: TextView = itemView.findViewById(R.id.calorieTextView)
        val averageSpeedTextView: TextView = itemView.findViewById(R.id.averageSpeedTextView)
    }
}