package com.epfl.drawyourpath.path

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.runStats.RunInfoStatsFragment
import com.epfl.drawyourpath.utils.Utils
import com.epfl.drawyourpath.utils.Utils.getStaticMapUrl
import com.google.android.gms.maps.model.LatLng

/**
 * This class is the adapter for the RecyclerView that displays the list of runs.
 */
class RunsAdapter(private var runs: List<Run>) : RecyclerView.Adapter<RunsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.past_run, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = runs[position]

        // TODO: will be refactor with the creation of a bitmap directly with the run coordinates

        val runCoordinates: List<LatLng> = run.getPath().getPoints().flatten() // Get the coordinates for this specific run
        val apiKey = "AIzaSyCE8covSYZE_sOv4Z-HaoljRlNOTV8cKRk"

        val staticMapUrl = getStaticMapUrl(runCoordinates, apiKey)

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(staticMapUrl)
            .placeholder(R.drawable.map_loading_placeholder) // Set a placeholder image while loading
            .into(holder.mapImageView)

        // Set the data to the view items in the layout
        // holder.mapImageView.setImageResource(run.mapImage)
        holder.dateTextView.text = run.getDate()
        holder.distanceTextView.text = holder.context.getString(R.string.display_distance).format(Utils.getStringDistance(run.getDistance()))
        holder.timeTakenTextView.text = holder.context.getString(R.string.display_duration).format(Utils.getStringDuration(run.getDuration()))
        holder.shapeRecognizedTextView.text = holder.context.getString(R.string.display_shape).format(run.predictedShape)
        holder.shapeScoreTextView.text = holder.context.getString(R.string.display_score).format(run.similarityScore)
        holder.itemView.setOnClickListener {
            val activity: MainActivity = holder.itemView.context as MainActivity
            val fragTransaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.fragmentContainerView, RunInfoStatsFragment(run = run)).commit()
        }
    }

    override fun getItemCount(): Int {
        return runs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mapImageView: ImageView = itemView.findViewById(R.id.mapImageView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        val timeTakenTextView: TextView = itemView.findViewById(R.id.timeTakenTextView)
        val shapeRecognizedTextView: TextView = itemView.findViewById(R.id.shapeTextView)
        val shapeScoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val context: Context = itemView.context
    }

    fun updateRunsData(newRuns: List<Run>) {
        runs = newRuns
        notifyDataSetChanged()
    }
}
