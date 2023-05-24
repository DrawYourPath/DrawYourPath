package com.epfl.drawyourpath.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.epfl.drawyourpath.R

class TournamentArrayAdapter(context: Context, @LayoutRes val resource: Int = R.layout.item_tournament_selection) :
    ArrayAdapter<Tournament>(context, resource, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        view.findViewById<TextView>(R.id.item_tournament_name).text = getItem(position)!!.name
        view.findViewById<TextView>(R.id.item_tournament_description).text = getItem(position)!!.description

        return view
    }


}