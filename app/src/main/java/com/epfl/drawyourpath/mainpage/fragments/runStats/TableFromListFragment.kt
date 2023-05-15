package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.marginRight
import com.epfl.drawyourpath.R

/**
 * This fragment is used to displayed the elements of the given map in a table of two columns
 * @param map that we would to display the content of string contents
 * @param column1Name name of the column 1
 * @param column2Name name of the column 2
 */
class TableFromListFragment(private val map: Map<String, String>, private val column1Name: String, private val column2Name: String) : Fragment(R.layout.fragment_table_from_list) {
    private lateinit var tableView: TableLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.tableView = view.findViewById(R.id.tableFromList)
        //add column titles
        addColumnsTitle()
        //add the information present in the map
        addMapInfo()
    }
    /**
     * Helper functions to add the tile of the columns of table
     */
    private fun addColumnsTitle(){
        val row = TableRow(context)
        //for the title 1
        val title1 = TextView(context)
        title1.text = this.column1Name
        title1.setTextColor(Color.BLUE)
        title1.setPadding(30,0,0,0)
        title1.textSize = 16F
        row.addView(title1)
        //add a vertical line
        val verticalLine = View(context)
        verticalLine.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, 2)
        verticalLine.setBackgroundColor(Color.BLACK)
        row.addView(verticalLine)
        //for the title 2
        val title2 = TextView(context)
        title2.text = this.column2Name
        title2.setPadding(30,0,0,0)
        title2.setTextColor(Color.BLUE)
        title2.textSize = 16F
        row.addView(title2)
        this.tableView.addView(row)
    }
    /**
     * Helper function to add the information present in the map to the table
     */
    private fun addMapInfo(){
        map.forEach{key, value ->
            val row = TableRow(context)
            //for the key
            val text1 = TextView(context)
            text1.text = key
            text1.setTextColor(Color.BLACK)
            text1.setPadding(30,0,0,0)
            text1.textSize = 10F
            row.addView(text1)
            //for the value
            val text2 = TextView(context)
            text2.text = value
            text2.setTextColor(Color.BLACK)
            text2.textSize = 10F
            text2.setPadding(30,0,0,0)
            row.addView(text2)
            this.tableView.addView(row)
        }
    }
}