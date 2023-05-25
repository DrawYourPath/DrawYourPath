package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run

class RunPopupAdapter(private var runs: List<Run>, private val runClickListener: (Run) -> Unit) :
    RecyclerView.Adapter<RunPopupAdapter.RunPopupViewHolder>() {

    // ViewHolder class for displaying each run
    class RunPopupViewHolder(itemView: View, val runClickListener: (Run) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val runTitle: TextView = itemView.findViewById(R.id.runTitle)

        fun bind(run: Run) {
            runTitle.text = "${run.getDate()} - ${run.getDistance()}km"
            itemView.setOnClickListener {
                runClickListener(run)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunPopupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_run_popup, parent, false)
        return RunPopupViewHolder(view, runClickListener)
    }

    override fun onBindViewHolder(holder: RunPopupViewHolder, position: Int) {
        holder.bind(runs[position])
    }

    override fun getItemCount() = runs.size

    fun updateRunsData(newRuns: List<Run>) {
        this.runs = newRuns
        notifyDataSetChanged()
    }
}
