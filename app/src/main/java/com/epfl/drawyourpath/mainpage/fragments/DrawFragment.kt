package com.epfl.drawyourpath.mainpage.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.login.LoginActivity
import com.epfl.drawyourpath.pathDrawing.PathDrawingActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DrawFragment : Fragment(R.layout.fragment_draw), OnMapReadyCallback {
    // TODO: add Google Map UI here and the ViewModel for this screen/fragment.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_draw_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //display the activity to draw a path when we click on start drawing button
        val startButton: Button = view.findViewById(R.id.button_start_drawing)
        startButton.setOnClickListener {
            val intent = Intent(this.context, PathDrawingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val coordinates = LatLng(46.5185, 6.56177)
        map.addMarker(
            MarkerOptions()
                .position(coordinates)
                .title("EPFL"),
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15F))
    }
}
