package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * This fragment is used to display the information containing in the given map on a graph
 * @param map that contains the information that we would like to displayed
 * @param titleAxe1 title of the first axe
 * @param titleAxe2 title of the second axe
 */
class GraphFromListFragment(private val map: Map<Double, Double>, private val titleAxe1: String, private val titleAxe2: String) : Fragment(R.layout.fragment_graph_from_list) {
    private lateinit var graphView: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.graphView = view.findViewById(R.id.graphFromList)
        val entries = ArrayList<Entry>()

        map.forEach { key, value ->
            entries.add(Entry(key.toFloat(), value.toFloat()))
        }

        val listPoints = LineDataSet(entries, "$titleAxe2 in function of the $titleAxe1")
        listPoints.setDrawValues(false)
        listPoints.setDrawFilled(true)
        listPoints.lineWidth = 3f

        graphView.data = LineData(listPoints)
        graphView.axisRight.isEnabled = false

        graphView.setTouchEnabled(true)
        graphView.setPinchZoom(true)

        graphView.description.text = ""
    }
}
