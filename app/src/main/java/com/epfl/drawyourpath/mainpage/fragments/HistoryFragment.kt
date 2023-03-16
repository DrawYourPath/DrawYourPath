package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.path.Path
import com.github.drawyourpath.bootcamp.path.Run
import com.github.drawyourpath.bootcamp.path.RunsAdapter
import com.google.android.gms.maps.model.LatLng


class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var runsAdapter: RunsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val runsRecyclerView = view.findViewById<RecyclerView>(R.id.runsRecyclerView)
        runsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val runs = getRunsData() // replace this with your own function to get the list of runs
        runsAdapter = RunsAdapter(runs)
        runsRecyclerView.adapter = runsAdapter

        return view
    }

    /**
     * This function is a dummy function to get some dummy data for the list of runs.
     * Replace this with your own function to get the list of runs.
     */
    private fun getRunsData(): List<Run> {

        val runs = mutableListOf<Run>()

        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(point1, point2)
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10
        val run1 = Run(path, startTime, endTime)
        runs.add(run1)

        val point3 = LatLng(0.0, 0.0)
        val point4 = LatLng(0.05, 0.0)
        val points2 = listOf(point3, point4)
        val path2 = Path(points2)
        val startTime2 = System.currentTimeMillis()
        val endTime2 = startTime + 10
        val run2 = Run(path2, startTime2, endTime2)
        runs.add(run2)

        return runs
    }

}